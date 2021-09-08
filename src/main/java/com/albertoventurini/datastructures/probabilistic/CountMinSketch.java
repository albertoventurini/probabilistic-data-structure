package com.albertoventurini.datastructures.probabilistic;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.LongAdder;

final class CountMinSketch {

    private final int width;
    private final int depth;
    private final Hash hash;

    private final AtomicLongArray sketch;

    private final LongAdder count = new LongAdder();

    CountMinSketch(final int width, final int depth, final Hash hash) {
        this.width = width;
        this.depth = depth;
        this.hash = hash;
        sketch = new AtomicLongArray(width * depth);
    }

    // As explained here: https://www.eecs.harvard.edu/~michaelm/postscripts/tr-02-05.pdf
    // it is possible to simulate n hash functions using 2 hash functions.
    private long[] calculateHashes(final String string) {
        final long[] hash = this.hash.calculate(string);

        if (depth <= 2) {
            return new long[]{Math.abs(hash[0] % width), Math.abs(hash[1] % width)};
        }

        final long[] result = new long[depth];
        for (int i = 0; i < depth; ++i) {
            result[i] = Math.abs((hash[0] + (long) i * hash[1]) % width);
        }
        return result;
    }

    long update(final String string, long value) {
        long min = Long.MAX_VALUE;
        long[] hashes = calculateHashes(string);
        for (int i = 0; i < hashes.length; i++) {
            long cell = sketch.getAndSet((int) (i * width + hashes[i]), value);
            if (cell < min) {
                min = cell;
            }
        }
        return min;
    }

    long increment(final String string) {
        count.increment();

        long min = Long.MAX_VALUE;
        long[] hashes = calculateHashes(string);
        for (int i = 0; i < hashes.length; i++) {
            long cell = sketch.getAndIncrement((int) (i * width + hashes[i]));
            if (cell < min) {
                min = cell;
            }
        }
        return min;
    }

    long get(final String string) {
        long min = Long.MAX_VALUE;
        long[] hashes = calculateHashes(string);
        for (int i = 0; i < hashes.length; i++) {
            long cell = sketch.get((int) (i * width + hashes[i]));
            if (cell < min) {
                min = cell;
            }
        }
        return min;
    }

    long get2(final String string) {
        long[] e = new long[depth];

        long[] hashes = calculateHashes(string);

        for (int i = 0; i < hashes.length; i++) {
            long cell = sketch.get((int) (i * width + hashes[i]));
            long noiseEstimation = (count.longValue() - cell) / (width - 1);
            e[i] = cell - noiseEstimation;
        }

        Arrays.sort(e);
        return e[e.length / 2];
    }
}
