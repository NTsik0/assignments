# Assignment 1 — Sorting 1,000,000 Integers in Java

Benchmarking four sorting algorithms using JMH (Java Microbenchmark Harness).

---

## Algorithms Implemented

| Algorithm | Complexity | Notes |
|-----------|-----------|-------|
| Bubble Sort | O(n²) | Early-exit optimisation; benchmarked at N=100,000 only |
| Quick Sort | O(n log n) avg | Median-of-three pivot, insertion sort fallback for ≤16 elements |
| LSD Radix Sort | O(4n) | Base-256, 4 passes, supports negative numbers via sign-bit XOR |
| Arrays.sort() | O(n log n) | JDK Dual-Pivot Quick Sort, used as reference |

---

## Project Structure

```
sorting-benchmark/
├── pom.xml
├── Sorting_Benchmark_Report_FINAL.pdf
└── src/main/java/
    ├── sorting/
    │   ├── BubbleSort.java
    │   ├── QuickSort.java
    │   ├── LsdRadixSort.java
    │   ├── SortVerifier.java
    │   ├── DatasetGenerator.java
    │   └── CorrectnessCheck.java
    └── benchmark/
        └── SortingBenchmark.java
```

---

## Build & Run

```bash
# Verify correctness
mvn compile
java -cp target/classes sorting.CorrectnessCheck

# Run benchmarks (Bubble Sort excluded at 1M — see report Section 6)
mvn clean package
java -jar target/benchmarks.jar -e bubbleSort -rf json -rff results.json
```

---

## Real JMH Results (N = 1,000,000, 20 samples each)

| Algorithm | Random | Sorted | Reversed | Nearly Sorted |
|-----------|--------|--------|----------|---------------|
| Bubble Sort | excluded | excluded | excluded | excluded |
| Quick Sort | 92.612 ± 0.903 ms | 11.978 ± 0.322 ms | 36.141 ± 0.598 ms | 40.972 ± 0.967 ms |
| LSD Radix Sort | **17.975 ± 1.353 ms** | 34.560 ± 2.141 ms | **33.167 ± 2.734 ms** | **31.465 ± 1.049 ms** |
| Arrays.sort() | 161.100 ± 14.765 ms | **1.291 ± 0.096 ms** | 4.422 ± 0.623 ms | 86.440 ± 3.652 ms |

---

## Key Findings

- **LSD Radix Sort** — fastest for random, reversed, and nearly sorted input
- **Arrays.sort()** — exceptionally fast on sorted input (1.291 ms, run detection)
- **Quick Sort** — lowest variance, most predictable latency
- **Bubble Sort** — O(n²) confirmed, excluded from 1M benchmark (see PDF report)

---

## Deliverables

- `sorting-benchmark/` — Java source code and JMH benchmark
- `Sorting_Benchmark_Report_FINAL.pdf` — analytical report
