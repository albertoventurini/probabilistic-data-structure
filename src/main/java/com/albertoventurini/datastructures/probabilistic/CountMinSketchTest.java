package com.albertoventurini.datastructures.probabilistic;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.summingInt;

public class CountMinSketchTest {

    public static void main(String[] args) throws Exception {
        final List<String> words =
                Arrays.stream(Files.readString(Path.of("corpus2.txt")).split(" ")).collect(Collectors.toList());

        // Calculate word frequencies with a map
        final Map<String, Integer> wordFrequencies = words
                .stream()
                .collect(Collectors.groupingBy(Function.identity(), summingInt(e -> 1)));

        // Calculate word frequencies with a count min sketch
        final CountMinSketch countMinSketch = new CountMinSketch(6000, 4, new MurmurHash());
        words.forEach(countMinSketch::increment);

        final List<Integer> errors = new ArrayList<>();

        // Query words
        words.stream().limit(100).forEach(w -> {
            final int freq1 = wordFrequencies.getOrDefault(w, 0);
            final int freq2 = (int) countMinSketch.get(w);

            System.out.println("HashMap " + w + " -> " + freq1);
            System.out.println("CountMinSketch " + w + " -> " + freq2);

            errors.add(Math.abs(freq1 - freq2));
        });

        Collections.sort(errors);
        final int medianError = errors.get(errors.size() / 2);
        final double avgError = errors.stream().mapToDouble(i -> (double) i).average().getAsDouble();

        System.out.println("\nMedian error: " + medianError);
        System.out.println("Average error: " + avgError);
    }
}
