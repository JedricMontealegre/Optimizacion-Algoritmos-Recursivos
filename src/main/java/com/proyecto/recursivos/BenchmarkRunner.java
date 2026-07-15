package com.proyecto.recursivos;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;

public final class BenchmarkRunner {
    private static final int[] STANDARD_SIZES = {
            128, 256, 512, 1_024, 2_048, 4_096, 8_192, 16_384
    };
    private static final int[] WORST_CASE_SIZES = {
            128, 256, 512, 1_024, 2_048, 3_072, 4_096
    };

    private BenchmarkRunner() {
    }

    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.US);
        Arguments arguments = Arguments.parse(args);

        List<BenchmarkResult> results = new ArrayList<>();
        results.addAll(runScenario(
                "MergeSort",
                "aleatorio",
                STANDARD_SIZES,
                arguments,
                BenchmarkRunner::randomArray,
                MergeSort::sort,
                Model.N_LOG_N));
        results.addAll(runScenario(
                "QuickSort",
                "balanceado",
                STANDARD_SIZES,
                arguments,
                BenchmarkRunner::sortedArray,
                QuickSort::sortMiddlePivot,
                Model.N_LOG_N));
        results.addAll(runScenario(
                "QuickSort",
                "peor_caso",
                WORST_CASE_SIZES,
                arguments,
                BenchmarkRunner::sortedArray,
                QuickSort::sortLastPivot,
                Model.N_SQUARED));

        Map<String, Regression> regressions = calculateRegressions(results);
        writeCsv(arguments.output, results, regressions);
        printSummary(arguments.output, results, regressions);
    }

    private static List<BenchmarkResult> runScenario(
            String algorithm,
            String scenario,
            int[] sizes,
            Arguments arguments,
            ArrayFactory arrayFactory,
            BiConsumer<int[], AlgorithmMetrics> sorter,
            Model model) {
        List<BenchmarkResult> results = new ArrayList<>();

        for (int n : sizes) {
            int[] source = arrayFactory.create(n);

            for (int i = 0; i < arguments.warmup; i++) {
                int[] warmupValues = Arrays.copyOf(source, source.length);
                sorter.accept(warmupValues, new AlgorithmMetrics());
                assertSorted(warmupValues);
            }

            long[] times = new long[arguments.repetitions];
            long[] heapDeltas = new long[arguments.repetitions];
            AlgorithmMetrics representativeMetrics = null;

            for (int repetition = 0; repetition < arguments.repetitions; repetition++) {
                requestGarbageCollection();
                int[] values = Arrays.copyOf(source, source.length);
                AlgorithmMetrics metrics = new AlgorithmMetrics();

                long start = System.nanoTime();
                sorter.accept(values, metrics);
                long elapsed = System.nanoTime() - start;

                assertSorted(values);
                times[repetition] = elapsed;
                heapDeltas[repetition] = metrics.peakHeapDeltaBytes();
                representativeMetrics = metrics;
            }

            Arrays.sort(times);
            Arrays.sort(heapDeltas);
            long median = times[times.length / 2];
            long medianHeap = heapDeltas[heapDeltas.length / 2];

            results.add(new BenchmarkResult(
                    algorithm,
                    scenario,
                    n,
                    median,
                    times[0],
                    times[times.length - 1],
                    medianHeap,
                    representativeMetrics.maxDepth(),
                    representativeMetrics.comparisons(),
                    representativeMetrics.writes(),
                    model.basis(n)));
        }
        return results;
    }

    private static Map<String, Regression> calculateRegressions(List<BenchmarkResult> results) {
        Map<String, List<BenchmarkResult>> grouped = new LinkedHashMap<>();
        for (BenchmarkResult result : results) {
            grouped.computeIfAbsent(key(result), ignored -> new ArrayList<>()).add(result);
        }

        Map<String, Regression> regressions = new LinkedHashMap<>();
        for (Map.Entry<String, List<BenchmarkResult>> entry : grouped.entrySet()) {
            entry.getValue().sort(Comparator.comparingInt(BenchmarkResult::n));
            regressions.put(entry.getKey(), Regression.fit(entry.getValue()));
        }
        return regressions;
    }

    private static void writeCsv(
            Path output,
            List<BenchmarkResult> results,
            Map<String, Regression> regressions) throws IOException {
        Path parent = output.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(output)) {
            writer.write("algorithm,scenario,n,median_ns,min_ns,max_ns,peak_heap_delta_bytes," +
                    "max_depth,comparisons,writes,model_basis,coefficient_ns_per_basis,r_squared");
            writer.newLine();

            for (BenchmarkResult result : results) {
                Regression regression = regressions.get(key(result));
                writer.write(String.format(Locale.US,
                        "%s,%s,%d,%d,%d,%d,%d,%d,%d,%d,%.6f,%.9f,%.6f",
                        result.algorithm(),
                        result.scenario(),
                        result.n(),
                        result.medianNanoseconds(),
                        result.minimumNanoseconds(),
                        result.maximumNanoseconds(),
                        result.peakHeapDeltaBytes(),
                        result.maximumDepth(),
                        result.comparisons(),
                        result.writes(),
                        result.modelBasis(),
                        regression.coefficient(),
                        regression.rSquared()));
                writer.newLine();
            }
        }
    }

    private static void printSummary(
            Path output,
            List<BenchmarkResult> results,
            Map<String, Regression> regressions) {
        System.out.println("Benchmark finalizado");
        System.out.println("Java: " + System.getProperty("java.version"));
        System.out.println("Sistema: " + System.getProperty("os.name") + " " +
                System.getProperty("os.arch"));
        System.out.println("Resultados: " + output.toAbsolutePath());
        System.out.println();

        for (Map.Entry<String, Regression> entry : regressions.entrySet()) {
            System.out.printf(Locale.US,
                    "%-28s c = %.9f ns/unidad, R² = %.4f%n",
                    entry.getKey(),
                    entry.getValue().coefficient(),
                    entry.getValue().rSquared());
        }

        BenchmarkResult deepest = results.stream()
                .max(Comparator.comparingInt(BenchmarkResult::maximumDepth))
                .orElseThrow();
        System.out.printf("Mayor profundidad observada: %d (%s, %s, n=%d)%n",
                deepest.maximumDepth(),
                deepest.algorithm(),
                deepest.scenario(),
                deepest.n());
    }

    private static int[] randomArray(int n) {
        Random random = new Random(31L * n + 17L);
        int[] values = new int[n];
        for (int i = 0; i < n; i++) {
            values[i] = random.nextInt(Math.max(2, n * 4));
        }
        return values;
    }

    private static int[] sortedArray(int n) {
        int[] values = new int[n];
        for (int i = 0; i < n; i++) {
            values[i] = i;
        }
        return values;
    }

    private static void assertSorted(int[] values) {
        for (int i = 1; i < values.length; i++) {
            if (values[i - 1] > values[i]) {
                throw new IllegalStateException("El algoritmo no ordenó correctamente el arreglo");
            }
        }
    }

    private static void requestGarbageCollection() {
        System.gc();
        try {
            Thread.sleep(3L);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("La ejecución fue interrumpida", exception);
        }
    }

    private static String key(BenchmarkResult result) {
        return result.algorithm() + "_" + result.scenario();
    }

    @FunctionalInterface
    private interface ArrayFactory {
        int[] create(int n);
    }

    private enum Model {
        N_LOG_N {
            @Override
            double basis(int n) {
                return n * (Math.log(n) / Math.log(2.0));
            }
        },
        N_SQUARED {
            @Override
            double basis(int n) {
                return (double) n * n;
            }
        };

        abstract double basis(int n);
    }

    private record Arguments(Path output, int repetitions, int warmup) {
        private static Arguments parse(String[] args) {
            Path output = Path.of("data", "benchmark-results.csv");
            int repetitions = 9;
            int warmup = 4;

            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "--output" -> {
                        requireValue(args, i);
                        output = Path.of(args[++i]);
                    }
                    case "--repetitions" -> {
                        requireValue(args, i);
                        repetitions = positiveInteger(args[++i], "repetitions");
                    }
                    case "--warmup" -> {
                        requireValue(args, i);
                        warmup = nonNegativeInteger(args[++i], "warmup");
                    }
                    case "--help" -> {
                        printHelpAndExit();
                    }
                    default -> throw new IllegalArgumentException(
                            "Argumento no reconocido: " + args[i] + ". Use --help.");
                }
            }
            return new Arguments(output, repetitions, warmup);
        }

        private static void requireValue(String[] args, int index) {
            if (index + 1 >= args.length) {
                throw new IllegalArgumentException("Falta el valor para " + args[index]);
            }
        }

        private static int positiveInteger(String text, String name) {
            int value = Integer.parseInt(text);
            if (value <= 0) {
                throw new IllegalArgumentException(name + " debe ser mayor que cero");
            }
            return value;
        }

        private static int nonNegativeInteger(String text, String name) {
            int value = Integer.parseInt(text);
            if (value < 0) {
                throw new IllegalArgumentException(name + " no puede ser negativo");
            }
            return value;
        }

        private static void printHelpAndExit() {
            System.out.println("Uso: java ... BenchmarkRunner [opciones]");
            System.out.println("  --output RUTA          Archivo CSV de salida");
            System.out.println("  --repetitions NUMERO   Repeticiones medidas por tamaño (predeterminado: 9)");
            System.out.println("  --warmup NUMERO        Ejecuciones de calentamiento JIT (predeterminado: 4)");
            System.exit(0);
        }
    }
}
