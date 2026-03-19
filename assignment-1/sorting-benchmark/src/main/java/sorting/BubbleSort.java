package sorting;

/** Bubble Sort implementation with optimization */

public final class BubbleSort {

    private BubbleSort() {}

    public static void sort(int[] a) {
        if (a == null || a.length < 2) return;

        int n = a.length;
        for (int pass = 0; pass < n - 1; pass++) {
            boolean swapped = false;

            // after each pass the largest unsorted element has bubbled to
            // its final position => shrink the new active place to 1

            for (int i = 0; i < n - 1 - pass; i++) {
                if (a[i] > a[i + 1]) {
                    int tmp = a[i];
                    a[i]     = a[i + 1];
                    a[i + 1] = tmp;
                    swapped = true;
                }
            }

            // if no swap occurred the array is already sorted
            if (!swapped) break;
        }
    }
}