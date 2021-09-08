package com.albertoventurini.datastructures.probabilistic;

import org.apache.commons.codec.digest.MurmurHash3;

public class Int32MurmurHash implements Hash {

    @Override
    public long[] calculate(final String string) {
        return new long[]{MurmurHash3.hash32(string), 0};
    }
}
