package com.albertoventurini.datastructures.probabilistic;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class BloomFilterTest {

    public static void main(String[] args) throws Exception {
        final List<String> words =
                Arrays.stream(Files.readString(Path.of("corpus2.txt")).split(" ")).collect(Collectors.toList());

        final Set<String> wordSet = new HashSet<>(words);

        final BloomFilter bloomFilter = new BloomFilter(2, 200000, new MurmurHash());

        words.forEach(bloomFilter::add);

        final List<Integer> errors = new ArrayList<>();

        // Query words
        words.stream().limit(100).forEach(w -> {
            final String variation = w + ThreadLocalRandom.current().nextInt('a', 'z');
            System.out.println("HashSet contains " + w + ": " + wordSet.contains(w));
            System.out.println("HashSet contains " + variation + ": " + wordSet.contains(variation));

            System.out.println("BloomFilter contains " + w + ": " + bloomFilter.contains(w));
            System.out.println("BloomFilter contains " + variation + ": " + bloomFilter.contains(variation));

            errors.add(wordSet.contains(w) != bloomFilter.contains(w) ? 1 : 0);
            errors.add(wordSet.contains(variation) != bloomFilter.contains(variation) ? 1 : 0);
        });

        final double avgError = errors.stream().mapToDouble(i -> (double) i).average().getAsDouble();
        System.out.println("Average error: " + avgError);
    }
}
