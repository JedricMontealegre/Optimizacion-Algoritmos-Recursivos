package com.proyecto.recursivos;

/** QuickSort recursivo con selección de pivote configurable. */
public final class QuickSort {
    private QuickSort() {
    }

    /**
     * Usa el último elemento como pivote. Un arreglo ordenado produce el peor caso.
     */
    public static void sortLastPivot(int[] values, AlgorithmMetrics metrics) {
        validate(values);
        if (values.length > 1) {
            sortLastPivot(values, 0, values.length - 1, metrics);
        }
    }

    /**
     * Usa el elemento central como pivote y lo mueve al final antes de particionar.
     * En arreglos ordenados genera particiones aproximadamente balanceadas.
     */
    public static void sortMiddlePivot(int[] values, AlgorithmMetrics metrics) {
        validate(values);
        if (values.length > 1) {
            sortMiddlePivot(values, 0, values.length - 1, metrics);
        }
    }

    private static void validate(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("El arreglo no puede ser null");
        }
    }

    private static void sortLastPivot(
            int[] values,
            int low,
            int high,
            AlgorithmMetrics metrics) {
        metrics.enterCall();
        try {
            if (low >= high) {
                return;
            }
            int pivot = partition(values, low, high, high, metrics);
            sortLastPivot(values, low, pivot - 1, metrics);
            sortLastPivot(values, pivot + 1, high, metrics);
        } finally {
            metrics.exitCall();
        }
    }

    private static void sortMiddlePivot(
            int[] values,
            int low,
            int high,
            AlgorithmMetrics metrics) {
        metrics.enterCall();
        try {
            if (low >= high) {
                return;
            }
            int middle = low + (high - low) / 2;
            int pivot = partition(values, low, high, middle, metrics);
            sortMiddlePivot(values, low, pivot - 1, metrics);
            sortMiddlePivot(values, pivot + 1, high, metrics);
        } finally {
            metrics.exitCall();
        }
    }

    private static int partition(
            int[] values,
            int low,
            int high,
            int pivotIndex,
            AlgorithmMetrics metrics) {
        swap(values, pivotIndex, high, metrics);
        int pivotValue = values[high];
        int smaller = low - 1;

        for (int current = low; current < high; current++) {
            metrics.addComparison();
            if (values[current] <= pivotValue) {
                smaller++;
                swap(values, smaller, current, metrics);
            }
        }
        swap(values, smaller + 1, high, metrics);
        return smaller + 1;
    }

    private static void swap(int[] values, int first, int second, AlgorithmMetrics metrics) {
        if (first == second) {
            return;
        }
        int temporary = values[first];
        values[first] = values[second];
        values[second] = temporary;
        metrics.addWrites(2);
    }
}
