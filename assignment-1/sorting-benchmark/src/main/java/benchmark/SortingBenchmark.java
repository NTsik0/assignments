package benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import sorting.*;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 5, timeUnit = TimeUnit.SECONDS)
@Fork(2)
@State(Scope.Benchmark)
public class SortingBenchmark {

    // parameters

    private static final int N    = 1_000_000;
    private static final long SEED = 42L;

    @Param({"UNIFORM_RANDOM", "SORTED", "REVERSE_SORTED", "NEARLY_SORTED"})
    public String distribution;

    // master copy generated once per trial for having benchmark to work on fresh copies
    private int[] master;


    @Setup(Level.Trial)
    public void setup() {
        master = switch (distribution) {

            case "UNIFORM_RANDOM" -> DatasetGenerator.uniformRandom(N, SEED);
            case "SORTED"         -> DatasetGenerator.sorted(N);
            case "REVERSE_SORTED" -> DatasetGenerator.reverseSorted(N);
            case "NEARLY_SORTED"  -> DatasetGenerator.nearlySorted(N, SEED);
            default -> throw new IllegalArgumentException("Unknown distribution: " + distribution);

        };
    }

    // Benchmark methods - one per algorithm

    @Benchmark
    public int[] bubbleSort() {
        int[] a = Arrays.copyOf(master, N);
        BubbleSort.sort(a);
        return a; // return in order to prevent dead code eliminatio
    }

    @Benchmark
    public int[] quickSort() {
        int[] a = Arrays.copyOf(master, N);
        QuickSort.sort(a);
        return a;
    }

    @Benchmark
    public int[] lsdRadixSort() {
        int[] a = Arrays.copyOf(master, N);
        LsdRadixSort.sort(a);
        return a;
    }

    @Benchmark
    public int[] arraysSort() {
        int[] a = Arrays.copyOf(master, N);
        Arrays.sort(a);
        return a;
    }
    @Benchmark
    public int[] bubbleSortSmall() {
        int[] a = Arrays.copyOf(master, Math.min(master.length, 100_000));
        BubbleSort.sort(a);
        return a;
    }

   // main entry point it is like Fat-Jar runner

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(SortingBenchmark.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(10)
                .forks(2)
                .build();
        new Runner(opt).run();
    }
}