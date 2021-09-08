package com.albertoventurini.datastructures.probabilistic;

/**
 * This class implements a thread-safe Bloom filter that allows adding strings
 * and checking the presence of strings.
 */
final class BloomFilter {
    private final int hashCount;
    private final ConcurrentAddOnlyBitSet bitSet;
    private final Hash hash;

    BloomFilter(final int hashCount, final int size, final Hash hash) {
        this.hashCount = hashCount;
        this.bitSet = new ConcurrentAddOnlyBitSet(size);
        this.hash = hash;
    }

    // As explained here: https://www.eecs.harvard.edu/~michaelm/postscripts/tr-02-05.pdf
    // it is possible to simulate n hash functions using 2 hash functions.
    private long[] calculateHashes(final String string) {
        final long[] hash = this.hash.calculate(string);

        final int max = bitSet.getCapacity();

        if (hashCount <= 2) {
            return new long[]{Math.abs(hash[0] % max), Math.abs(hash[1] % max)};
        }

        final long[] result = new long[hashCount];
        for (int i = 0; i < hashCount; ++i) {
            result[i] = Math.abs((hash[0] + (long) i * hash[1]) % max);
        }
        return result;
    }

    /**
     * Add a string to this Bloom filter
     *
     * @param key the string to add
     * @return true if the string was not already present in the Bloom filter
     */
    boolean add(final String key) {
        boolean notPresent = false;
        for (long hash : calculateHashes(key)) {
            notPresent |= bitSet.add(hash);
        }
        return notPresent;
    }

    /**
     * Test whether this Bloom filter contains a string
     *
     * @param key the string to test
     * @return true if the Bloom filter contains the string
     */
    boolean contains(final String key) {
        boolean present = true;
        for (long hash : calculateHashes(key)) {
            present &= bitSet.get(hash);
        }
        return present;
    }
}
