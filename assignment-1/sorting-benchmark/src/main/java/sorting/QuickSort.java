package sorting;

/** Quicksort implementation */

public final class QuickSort {

    private static final int INSERTION_THRESHOLD = 16;

    private QuickSort() {}

   // a is array parameter to be sorted
    public static void sort(int[] a) {
        if (a == null || a.length < 2) return;
        quickSort(a, 0, a.length - 1);
    }

    // recursion

    private static void quickSort(int[] a, int lo, int hi) {

        if (hi - lo < INSERTION_THRESHOLD) {

            insertionSort(a, lo, hi);
            return;

        }

        int pivotIndex = medianOfThree(a, lo, lo + (hi - lo) / 2, hi);
        int pivot = a[pivotIndex];

        // here pivot gets to an end
        swap(a, pivotIndex, hi);

        int p = partition(a, lo, hi - 1, pivot);

        // here pivot goes to final sorted place
        swap(a, p, hi);

        quickSort(a, lo, p - 1);
        quickSort(a, p + 1, hi);
    }

    // left elements <= right elements >

    private static int partition(int[] a, int lo, int hi, int pivot) {

        int i = lo - 1;
        for (int j = lo; j <= hi; j++) {

            if (a[j] <= pivot) {
                i++;
                swap(a, i, j);

            }
        }
        return i + 1;
    }


    // here are helpers
    private static int medianOfThree(int[] a, int i, int j, int k) {
        // Sorting 3 candidate positions
        if (a[i] > a[j]) swap(a, i, j);
        if (a[i] > a[k]) swap(a, i, k);
        if (a[j] > a[k]) swap(a, j, k);
        // a[j] is now the median.
        return j;
    }

    /** sorting using insertion sort */

    private static void insertionSort(int[] a, int lo, int hi) {

        for (int i = lo + 1; i <= hi; i++) {
            int key = a[i];
            int j = i - 1;
            while (j >= lo && a[j] > key) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;
        }

    }

    private static void swap(int[] a, int i, int j) {

        int tmp = a[i];
        a[i]    = a[j];
        a[j]    = tmp;

    }
}