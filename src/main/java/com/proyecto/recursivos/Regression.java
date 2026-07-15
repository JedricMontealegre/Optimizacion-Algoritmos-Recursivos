package com.proyecto.recursivos;

import java.util.List;

/** Ajuste por mínimos cuadrados sin intercepto: tiempo = c * baseTeorica. */
public record Regression(double coefficient, double rSquared) {
    public static Regression fit(List<BenchmarkResult> results) {
        if (results == null || results.size() < 2) {
            return new Regression(Double.NaN, Double.NaN);
        }

        double sumXY = 0.0;
        double sumXX = 0.0;
        double meanY = 0.0;

        for (BenchmarkResult result : results) {
            sumXY += result.modelBasis() * result.medianNanoseconds();
            sumXX += result.modelBasis() * result.modelBasis();
            meanY += result.medianNanoseconds();
        }
        meanY /= results.size();

        double coefficient = sumXY / sumXX;
        double residualSumSquares = 0.0;
        double totalSumSquares = 0.0;

        for (BenchmarkResult result : results) {
            double predicted = coefficient * result.modelBasis();
            double residual = result.medianNanoseconds() - predicted;
            residualSumSquares += residual * residual;

            double centered = result.medianNanoseconds() - meanY;
            totalSumSquares += centered * centered;
        }

        double rSquared = totalSumSquares == 0.0
                ? 1.0
                : 1.0 - residualSumSquares / totalSumSquares;
        return new Regression(coefficient, rSquared);
    }
}
