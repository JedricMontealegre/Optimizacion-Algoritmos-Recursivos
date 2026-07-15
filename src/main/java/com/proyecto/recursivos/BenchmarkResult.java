package com.proyecto.recursivos;

public record BenchmarkResult(
        String algorithm,
        String scenario,
        int n,
        long medianNanoseconds,
        long minimumNanoseconds,
        long maximumNanoseconds,
        long peakHeapDeltaBytes,
        int maximumDepth,
        long comparisons,
        long writes,
        double modelBasis) {
}
