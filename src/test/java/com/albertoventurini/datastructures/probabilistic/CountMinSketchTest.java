package com.albertoventurini.datastructures.probabilistic;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class CountMinSketchTest {

    private final Set<String> hostnames;

    public CountMinSketchTest() throws Exception {
        final List<String> hostnames = Files.readAllLines(Path.of("hostnames2.csv"))
                .stream()
                .filter(l -> l.indexOf('"') == 0 && l.lastIndexOf('"') > 0)
                .map(l -> l.substring(1, l.lastIndexOf('"')))
                .collect(Collectors.toList());

        this.hostnames = new HashSet<>();

        for (int i = 0; i < 20; i++) {
            this.hostnames.add(hostnames.get(ThreadLocalRandom.current().nextInt(hostnames.size())));
        }
    }

    @Test
    public void testHash() {
        final CountMinSketch countMinSketch = new CountMinSketch(200, 2, new JavaHash());

        final Map<String, Integer> numbers = new HashMap<>();

        for (final String hostname : hostnames) {
            final int random = ThreadLocalRandom.current().nextInt(1000);
            numbers.put(hostname, random);
            countMinSketch.update(hostname, random);
        }

        int misses = 0;
        int error = 0;

        for (final String hostname : hostnames) {
            if (countMinSketch.get(hostname) != numbers.get(hostname)) {
                misses++;
                error += Math.abs(countMinSketch.get(hostname) - numbers.get(hostname));
            }
        }

        System.out.println("There were " + misses + " misses over " + hostnames.size() + " hostnames. Error = " + error);
    }
}
