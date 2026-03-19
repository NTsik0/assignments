package sorting;

import java.util.Arrays;

/** Correctness verifier for sorting implementations */

public final class SortVerifier {

    private SortVerifier() {}

    /** Returns a is sorted in non-decreasing order.*/

    public static boolean isSorted(int[] a) {

        for (int i = 1; i < a.length; i++) {
            if (a[i - 1] > a[i]) {
                return false;
            }
        }

        return true;
    }

    /** contains the same elements as in sorted order */

    public static void assertCorrect(int[] original, int[] actual) {
        int[] expected = Arrays.copyOf(original, original.length);
        Arrays.sort(expected);

        if (!isSorted(actual)) {
            throw new AssertionError("Array is not sorted");
        }
        if (!Arrays.equals(actual, expected)) {
            throw new AssertionError("Sorted result differs from reference output");
        }
    }
}