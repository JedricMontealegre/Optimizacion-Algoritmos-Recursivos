package com.proyecto.recursivos;

/**
 * Prueba opcional para aproximar el tamaño máximo antes de StackOverflowError.
 * El resultado depende de la JVM, el sistema operativo y la opción -Xss.
 */
public final class StackLimitProbe {
    private StackLimitProbe() {
    }

    public static void main(String[] args) {
        int start = args.length > 0 ? Integer.parseInt(args[0]) : 1_000;
        int step = args.length > 1 ? Integer.parseInt(args[1]) : 500;
        int maximum = args.length > 2 ? Integer.parseInt(args[2]) : 50_000;

        int lastSuccessful = 0;
        for (int n = start; n <= maximum; n += step) {
            int[] values = new int[n];
            for (int i = 0; i < n; i++) {
                values[i] = i;
            }

            try {
                AlgorithmMetrics metrics = new AlgorithmMetrics();
                QuickSort.sortLastPivot(values, metrics);
                lastSuccessful = n;
                System.out.printf("n=%d completado; profundidad=%d%n", n, metrics.maxDepth());
            } catch (StackOverflowError error) {
                System.out.printf("StackOverflowError en n=%d. Último n exitoso=%d%n", n, lastSuccessful);
                return;
            }
        }
        System.out.printf("No ocurrió StackOverflowError hasta n=%d%n", maximum);
    }
}
