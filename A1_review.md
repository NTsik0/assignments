## Summary

This is a strong Assignment 1 submission with real JMH setup, a compliant radix implementation, explicit correctness verification, and a PDF report. The main weakness is benchmark clarity: the benchmark class mixes a full-size Bubble Sort benchmark with a separate reduced-size Bubble Sort benchmark, which makes the intended scope harder to interpret.

## Strengths

- `assignment-1/sorting-benchmark/pom.xml` contains real JMH dependencies and shade-plugin packaging.
- `assignment-1/sorting-benchmark/src/main/java/benchmark/SortingBenchmark.java` benchmarks all required input distributions and includes `Arrays.sort(int[])`.
- `assignment-1/sorting-benchmark/src/main/java/sorting/LsdRadixSort.java` is a base-`256`, four-pass LSD radix sort with signed-int handling in the final pass.
- `assignment-1/sorting-benchmark/src/main/java/sorting/CorrectnessCheck.java` and `assignment-1/sorting-benchmark/src/main/java/sorting/SortVerifier.java` evidence both sortedness checking and comparison against `Arrays.sort()`.
- `assignment-1/sorting-benchmark/Sorting_Benchmark_Report.pdf` is present.

## Findings

- Moderate: `assignment-1/sorting-benchmark/src/main/java/benchmark/SortingBenchmark.java` includes both `bubbleSort()` on the full `1,000,000`-element master array and a separate `bubbleSortSmall()` on a `100,000`-element copy. That makes the Bubble Sort benchmark scope ambiguous instead of cleanly separating the practical reduced-size Bubble Sort path from the scalable algorithms.

## Requirement Checklist

- Java source code: Present
- Real JMH benchmark source: Present
- Build descriptor operational as submitted: Present
- PDF analytical report: Present
- Bubble Sort with early exit: Present
- In-place Quick Sort with identifiable pivot strategy: Present
- LSD Radix Sort, base `256`, `4` passes, negative support: Present
- `Arrays.sort(int[])` benchmark/reference: Present
- Uniform random dataset: Present
- Sorted dataset: Present
- Reverse-sorted dataset: Present
- Nearly sorted dataset with about `1%` swaps: Present
- Correctness check against `Arrays.sort()`: Present
- Explicit sortedness check: Present

## Verdict

This repository is technically strong and largely complete. The main improvement needed is to simplify and clarify the Bubble Sort benchmark scope.
