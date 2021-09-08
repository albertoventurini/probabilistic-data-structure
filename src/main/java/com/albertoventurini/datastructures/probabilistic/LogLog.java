package com.albertoventurini.datastructures.probabilistic;

// See https://storage.googleapis.com/pub-tools-public-publication-data/pdf/40671.pdf
public class LogLog {

    private final int m;
    private final Hash hash;
    private final long[] M;
    private final double alpha;

    // Number of bits to reserve for bucket index
    private final int k;

    public LogLog(final int k, final Hash hash) {
        this.k = k;
        this.m = 1 << k;
        this.hash = hash;
        M = new long[m];
        alpha = m == 16 ? 0.673 : m == 32 ? 0.697 : m == 64 ? 0.709 : 0.7213 / (1 + 1.079 / m);
    }

    public void add(final String string) {
        final int hash = (int) (this.hash.calculate(string)[0]);
        final int bucketIdx = calculateBucketIdx(hash);
        final int leadingZeros = calculateLeadingZeros(hash);
        M[bucketIdx] = Math.max(M[bucketIdx], leadingZeros);
    }

    public long estimateCount() {
        double c = 0.0;
        for (final long l : M) {
            c += 1.0 / (1 << l);  //Math.pow(2, l);
        }

        double estimate = alpha * m * m / c;

        if (estimate <= 5.0/2.0 * m) {
            double V = 0;
            for (final long l : M) {
                if (l == 0) V++;
            }
            if (V > 0) {
                estimate = m * Math.log(m / V);
            } else if (estimate > 1.0/30*(0x100000000L)) {
                estimate = - 0x100000000L * Math.log(1 - estimate/0x100000000L);
            }
        }

        return (long) estimate;
    }

    private int calculateBucketIdx(final int l) {
        // Extract k most significant bits
        return (int) l >>> (32 - k);
    }

    // leading zeros plus one, as per the paper
    private int calculateLeadingZeros(final int l) {
        int i = 1;
        int mask = 0x80000000 >>> k;
        while (mask > 0 && (l & mask) == 0) {
            mask = mask >>> 1;
            i++;
        }
        return i;
    }
}
