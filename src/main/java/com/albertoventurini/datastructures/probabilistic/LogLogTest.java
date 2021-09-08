package com.albertoventurini.datastructures.probabilistic;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LogLogTest {

    public static void main(String[] args) throws Exception {
        final List<String> words =
                Arrays.stream(Files.readString(Path.of("corpus2.txt")).split(" ")).collect(Collectors.toList());

        final Set<String> wordSet = new HashSet<>(words);

        final LogLog logLog = new LogLog(5, new Int32MurmurHash());
        words.forEach(logLog::add);

        System.out.println(wordSet.size());
        System.out.println(logLog.estimateCount());
    }
}
