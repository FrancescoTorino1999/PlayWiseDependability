package com.games.games_project.benchmark;

import com.games.games_project.utils.RatingComparator;
import org.openjdk.jmh.annotations.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 2)
@Measurement(iterations = 3)
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class RatingComparatorBenchmark {

    private RatingComparator comparator;
    private List<String> ratings;

    @Setup(Level.Trial)
    public void setup() {
        comparator = new RatingComparator();
        ratings = List.of("AO", "M", "T", "E10+", "E", "K-A", "NR", "RP");
    }

    @Benchmark
    public int benchmarkCompareCommonRatings() {
        return comparator.compare("M", "T");
    }

    @Benchmark
    public int benchmarkCompareUnrecognizedRatings() {
        return comparator.compare("XYZ", "NR");
    }

    @Benchmark
    public void benchmarkSortRatings() {
        List<String> list = new ArrayList<>(ratings);
        list.sort(comparator);
    }
}
