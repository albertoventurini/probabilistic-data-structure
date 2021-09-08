package com.albertoventurini.datastructures.probabilistic;

public class JavaHash implements Hash {

    @Override
    public long[] calculate(final String string) {
        final int hash = string.hashCode();
        final int hash1 = hash & 0x0000FFFF;
        final int hash2 = hash >>> 16;
        return new long[]{hash1, hash2};
    }
}
