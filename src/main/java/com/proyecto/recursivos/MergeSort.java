package com.proyecto.recursivos;

/** MergeSort recursivo instrumentado. */
public final class MergeSort {
    private MergeSort() {
    }

    public static void sort(int[] values, AlgorithmMetrics metrics) {
        if (values == null) {
            throw new IllegalArgumentException("El arreglo no puede ser null");
        }
        if (values.length < 2) {
            return;
        }
        int[] auxiliary = new int[values.length];
        metrics.observeHeap();
        sort(values, auxiliary, 0, values.length - 1, metrics);
    }

    private static void sort(
            int[] values,
            int[] auxiliary,
            int left,
            int right,
            AlgorithmMetrics metrics) {
        metrics.enterCall();
        try {
            if (left >= right) {
                return;
            }

            int middle = left + (right - left) / 2;
            sort(values, auxiliary, left, middle, metrics);
            sort(values, auxiliary, middle + 1, right, metrics);
            merge(values, auxiliary, left, middle, right, metrics);
        } finally {
            metrics.exitCall();
        }
    }

    private static void merge(
            int[] values,
            int[] auxiliary,
            int left,
            int middle,
            int right,
            AlgorithmMetrics metrics) {
        for (int index = left; index <= right; index++) {
            auxiliary[index] = values[index];
            metrics.addWrite();
        }

        int i = left;
        int j = middle + 1;
        int k = left;

        while (i <= middle && j <= right) {
            metrics.addComparison();
            if (auxiliary[i] <= auxiliary[j]) {
                values[k++] = auxiliary[i++];
            } else {
                values[k++] = auxiliary[j++];
            }
            metrics.addWrite();
        }

        while (i <= middle) {
            values[k++] = auxiliary[i++];
            metrics.addWrite();
        }

        while (j <= right) {
            values[k++] = auxiliary[j++];
            metrics.addWrite();
        }
    }
}
