package com.proyecto.recursivos;

/**
 * Métricas instrumentadas durante la ejecución de un algoritmo recursivo.
 */
public final class AlgorithmMetrics {
    private int currentDepth;
    private int maxDepth;
    private long comparisons;
    private long writes;
    private final long baselineHeap;
    private long peakHeap;

    public AlgorithmMetrics() {
        Runtime runtime = Runtime.getRuntime();
        this.baselineHeap = runtime.totalMemory() - runtime.freeMemory();
        this.peakHeap = this.baselineHeap;
    }

    public void enterCall() {
        currentDepth++;
        if (currentDepth > maxDepth) {
            maxDepth = currentDepth;
        }
        observeHeap();
    }

    public void exitCall() {
        currentDepth--;
    }

    public void addComparison() {
        comparisons++;
    }

    public void addWrite() {
        writes++;
    }

    public void addWrites(long amount) {
        writes += amount;
    }

    public void observeHeap() {
        Runtime runtime = Runtime.getRuntime();
        long used = runtime.totalMemory() - runtime.freeMemory();
        if (used > peakHeap) {
            peakHeap = used;
        }
    }

    public int maxDepth() {
        return maxDepth;
    }

    public long comparisons() {
        return comparisons;
    }

    public long writes() {
        return writes;
    }

    public long peakHeapDeltaBytes() {
        return Math.max(0L, peakHeap - baselineHeap);
    }
}
