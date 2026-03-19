package sorting;

/** Radix Sort for 32-bit signed integers. */

public final class LsdRadixSort {

    private static final int PASSES   = 4;   // 4 bytes × 8 bits = 32 bits
    private static final int RADIX    = 256; // base-256

    private LsdRadixSort() {}

    public static void sort(int[] a) {
        if (a == null || a.length < 2) return;

        final int n = a.length;
        int[] buf = new int[n];

        for (int pass = 0; pass < PASSES; pass++) {
            final int shift = pass * 8;
            final boolean lastPass = (pass == PASSES - 1);

            // 1) frequency table
            int[] count = new int[RADIX + 1]; // count[b+1] = frequency of bucket b
            for (int v : a) {
                int bucket = bucket(v, shift, lastPass);
                count[bucket + 1]++;
            }

            // 2) prefix-sum
            for (int b = 0; b < RADIX; b++) {
                count[b + 1] += count[b];
            }

            // 3) scatter
            for (int v : a) {
                int bucket = bucket(v, shift, lastPass);
                buf[count[bucket]++] = v;
            }

            // 4) copy back
            System.arraycopy(buf, 0, a, 0, n);
        }
    }


    private static int bucket(int v, int shift, boolean lastPass) {
        int byteVal = (v >>> shift) & 0xFF;
        if (lastPass) {
            byteVal ^= 0x80; // flipping sign bit for correct signed ordering
        }
        return byteVal;
    }
}