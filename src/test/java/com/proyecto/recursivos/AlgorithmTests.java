package com.proyecto.recursivos;

import java.util.Arrays;
import java.util.Random;

/** Pruebas sin dependencias externas; se ejecutan con assertions habilitadas. */
public final class AlgorithmTests {
    private AlgorithmTests() {
    }

    public static void main(String[] args) {
        testMergeSort();
        testQuickSortMiddlePivot();
        testQuickSortLastPivot();
        testEmptyAndSingleElementArrays();
        System.out.println("Todas las pruebas finalizaron correctamente.");
    }

    private static void testMergeSort() {
        int[] values = randomValues(2_000, 123L);
        int[] expected = values.clone();
        Arrays.sort(expected);
        MergeSort.sort(values, new AlgorithmMetrics());
        assert Arrays.equals(values, expected) : "MergeSort produjo un resultado incorrecto";
    }

    private static void testQuickSortMiddlePivot() {
        int[] values = randomValues(2_000, 456L);
        int[] expected = values.clone();
        Arrays.sort(expected);
        QuickSort.sortMiddlePivot(values, new AlgorithmMetrics());
        assert Arrays.equals(values, expected) : "QuickSort balanceado produjo un resultado incorrecto";
    }

    private static void testQuickSortLastPivot() {
        int[] values = randomValues(1_000, 789L);
        int[] expected = values.clone();
        Arrays.sort(expected);
        QuickSort.sortLastPivot(values, new AlgorithmMetrics());
        assert Arrays.equals(values, expected) : "QuickSort con último pivote produjo un resultado incorrecto";
    }

    private static void testEmptyAndSingleElementArrays() {
        MergeSort.sort(new int[0], new AlgorithmMetrics());
        QuickSort.sortMiddlePivot(new int[] {42}, new AlgorithmMetrics());
        QuickSort.sortLastPivot(new int[] {42}, new AlgorithmMetrics());
    }

    private static int[] randomValues(int size, long seed) {
        Random random = new Random(seed);
        int[] values = new int[size];
        for (int i = 0; i < size; i++) {
            values[i] = random.nextInt(10_000);
        }
        return values;
    }
}
