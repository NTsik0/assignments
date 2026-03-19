package sorting;

import java.util.Random;

/** Generates four input distributions for out benchmarks */

public final class DatasetGenerator {

    private DatasetGenerator() {}

    /** Uniform random integers in the full range */

    public static int[] uniformRandom(int n, long seed) {

        Random rng = new Random(seed);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = rng.nextInt();
        return a;

    }

    /** sorted ascending from 0 to (n-1) */
    public static int[] sorted(int n) {

        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = i;
        return a;

    }

    /** sorted descending  from (n-1),(n-2) to zero */
    public static int[] reverseSorted(int n) {

        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = n - 1 - i;
        return a;

    }

    /** starts sorted then performs random swaps , here we have n for array length parameter and seed for reproducing RNG */

    public static int[] nearlySorted(int n, long seed) {

        int[] a = sorted(n);
        Random rng = new Random(seed);
        int swaps = Math.max(1, n / 100); // ~1% of elements
        for (int s = 0; s < swaps; s++) {
            int i = rng.nextInt(n);
            int j = rng.nextInt(n);
            int tmp = a[i]; a[i] = a[j]; a[j] = tmp;

        }
        return a;
    }
}