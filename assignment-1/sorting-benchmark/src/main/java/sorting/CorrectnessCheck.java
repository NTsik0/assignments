package sorting;

import java.util.Arrays;

 /** this correctness checker is to confirm that algorithms give correct result before doing benchmark */

public final class CorrectnessCheck {

    private static final int N    = 1000;
    private static final long SEED = 42L;

    private CorrectnessCheck() {}

    public static void main(String[] args) {
        String[] distributions = {"UNIFORM_RANDOM", "SORTED", "REVERSE_SORTED", "NEARLY_SORTED"};

        for (String dist : distributions) {
            System.out.println("= Distribution: " + dist + " = ");
            int[] original = generate(dist);

            verify("BubbleSort",   original, bubbleSortCopy(original));
            verify("QuickSort",    original, quickSortCopy(original));
            verify("LsdRadixSort", original, radixSortCopy(original));
            verify("Arrays.sort",  original, arraysSortCopy(original));

            System.out.println();
        }
        System.out.println("All checks successfully passed.");
    }

// these are helpers

    private static int[] generate(String dist) {
        return switch (dist) {
            case "UNIFORM_RANDOM" -> DatasetGenerator.uniformRandom(N, SEED);
            case "SORTED"         -> DatasetGenerator.sorted(N);
            case "REVERSE_SORTED" -> DatasetGenerator.reverseSorted(N);
            case "NEARLY_SORTED"  -> DatasetGenerator.nearlySorted(N, SEED);
            default -> throw new IllegalArgumentException(dist);
        };
    }

    private static int[] bubbleSortCopy(int[] src) {
        int[] a = Arrays.copyOf(src, src.length);
        BubbleSort.sort(a);
        return a;
    }

    private static int[] quickSortCopy(int[] src) {
        int[] a = Arrays.copyOf(src, src.length);
        QuickSort.sort(a);
        return a;
    }

    private static int[] radixSortCopy(int[] src) {
        int[] a = Arrays.copyOf(src, src.length);
        LsdRadixSort.sort(a);
        return a;
    }

    private static int[] arraysSortCopy(int[] src) {
        int[] a = Arrays.copyOf(src, src.length);
        Arrays.sort(a);
        return a;
    }

//    here we have verifier sort passes or not, if not catches error message

    private static void verify(String name, int[] original, int[] actual) {
        try {
            SortVerifier.assertCorrect(original, actual);
            System.out.printf("  %-16s PASS%n", name);
        } catch (AssertionError e) {
            System.out.printf("  %-16s FAIL — %s%n", name, e.getMessage());
        }
    }
}