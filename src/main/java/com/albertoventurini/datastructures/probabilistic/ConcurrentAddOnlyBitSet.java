package com.albertoventurini.datastructures.probabilistic;

import java.util.concurrent.atomic.AtomicLongArray;

/**
 * This class implements a thread-safe set that allows adding and checking the presence of a long value.
 * The set is backed by a bit sequence.
 */
final class ConcurrentAddOnlyBitSet {
    private static final int BASE = 64;
    private final int capacity;
    private final AtomicLongArray buckets;

    ConcurrentAddOnlyBitSet(final int capacity) {
        this.capacity = capacity;

        final int bucketsCount = (capacity / BASE) + 1;
        buckets = new AtomicLongArray(bucketsCount);

        for (int i = 0; i < buckets.length(); i++) {
            buckets.set(i, 0);
        }
    }

    public int getCapacity() {
        return capacity;
    }

    /**
     * Adds the given value to the set.
     *
     * @param value the value to add to the set
     * @return true if the set did not already contain the specified element
     */
    boolean add(final long value) {
        final int bucketIdx = (int) value / BASE;
        final int bitIdx = (int) value - (BASE * bucketIdx);
        return atomicSet(bucketIdx, bitIdx);
    }

    /**
     * Tests whether the given value is contained in the set.
     *
     * @param value the value to test
     * @return true if the set contains the specified element
     */
    boolean get(final long value) {
        final int bucketIdx = (int) value / BASE;
        final int bitIdx = (int) value - (BASE * bucketIdx);
        return atomicGet(bucketIdx, bitIdx);
    }

    private boolean atomicSet(final int bucketIdx, final int bitIdx) {
        final long mask = mask(bitIdx);
        return (buckets.getAndUpdate(bucketIdx, l -> l | mask) & mask) == 0;
    }

    private boolean atomicGet(final int bucketIdx, final int bitIdx) {
        final long mask = mask(bitIdx);
        return (buckets.get(bucketIdx) & mask) == mask;
    }

    private static long mask(final int id) {
        return 1L << id;
    }
}
