# Predicción y optimización de algoritmos recursivos

Proyecto académico para analizar la relación entre la complejidad teórica y el comportamiento real de algoritmos recursivos. La implementación estudia **MergeSort** y **QuickSort** mediante ecuaciones de recurrencia, mediciones de tiempo, conteo de operaciones, profundidad de recursión y uso aproximado de memoria.

## Objetivos

- Modelar el tiempo de ejecución con ecuaciones de diferencias.
- Verificar los órdenes de crecimiento `n log2(n)` y `n²`.
- Comparar MergeSort, QuickSort balanceado y QuickSort en peor caso.
- Medir el tiempo con `System.nanoTime()`.
- registrar profundidad máxima, comparaciones, escrituras y variación del heap.
- Identificar el riesgo de `StackOverflowError` en recursiones no balanceadas.

## Algoritmos y escenarios

| Algoritmo | Escenario | Entrada | Modelo esperado |
|---|---|---|---|
| MergeSort | Aleatorio | Arreglo pseudoaleatorio | `T(n) = 2T(n/2) + cn`, por tanto `Theta(n log n)` |
| QuickSort | Balanceado | Arreglo ordenado, pivote central | `T(n) = 2T(n/2) + cn`, por tanto `Theta(n log n)` |
| QuickSort | Peor caso | Arreglo ordenado, último elemento como pivote | `T(n) = T(n-1) + cn`, por tanto `Theta(n²)` |

## Requisitos

- JDK 17 o superior. El proyecto fue probado con OpenJDK 21.
- Bash para los scripts `.sh` en Linux/macOS, o CMD/PowerShell para los scripts `.bat` en Windows.
- No se requieren librerías externas.

## Estructura del repositorio

```text
.
├── data/                       Resultados CSV y prueba del límite de pila
├── docs/                       Reporte técnico y presentación base
├── results/                    Gráficas generadas a partir del benchmark
├── scripts/                    Compilación, ejecución y pruebas
├── src/main/java/              Código fuente principal
├── src/test/java/              Pruebas funcionales sin dependencias
├── .gitignore
├── LICENSE
├── README.md
└── REFERENCES.md
```

## Ejecución rápida

### Linux o macOS

```bash
chmod +x scripts/*.sh
./scripts/test.sh
./scripts/run.sh
```

### Windows

```bat
scripts\test.bat
scripts\run.bat
```

El benchmark genera el archivo:

```text
data/benchmark-results.csv
```

## Opciones de ejecución

```bash
./scripts/run.sh --output data/mi-resultado.csv --repetitions 9 --warmup 4
```

- `--output`: ruta del CSV de salida.
- `--repetitions`: cantidad de mediciones por tamaño.
- `--warmup`: ejecuciones previas para reducir el efecto de compilación JIT.

## Prueba opcional de StackOverflow

La clase `StackLimitProbe` ejecuta QuickSort en peor caso aumentando gradualmente el tamaño de entrada. El resultado depende de la JVM, el sistema operativo y el tamaño de pila configurado con `-Xss`.

```bash
./scripts/compile.sh
java -Xss1m -cp out/main com.proyecto.recursivos.StackLimitProbe 1000 500 20000
```

Parámetros:

1. Tamaño inicial.
2. Incremento entre pruebas.
3. Tamaño máximo.

## Resultados incluidos

La corrida de referencia incluida en `data/benchmark-results.csv` se realizó con OpenJDK 21, siete repeticiones medidas y tres calentamientos. Los ajustes obtenidos fueron:

| Escenario | Base del modelo | Coeficiente ajustado | R² |
|---|---:|---:|---:|
| MergeSort aleatorio | `n log2(n)` | 12.2777 ns/unidad | 0.9990 |
| QuickSort balanceado | `n log2(n)` | 3.2315 ns/unidad | 0.9903 |
| QuickSort peor caso | `n²` | 0.3238 ns/unidad | 0.9969 |

En una prueba con `-Xss1m`, QuickSort en peor caso completó `n = 7500` y produjo `StackOverflowError` en `n = 8000`. Este límite es experimental y no debe generalizarse a otras máquinas.

## Limitaciones

- `System.nanoTime()` mide tiempo transcurrido, pero no elimina por completo el ruido del sistema operativo, la compilación JIT ni la actividad del recolector de basura.
- `Runtime.totalMemory() - Runtime.freeMemory()` ofrece una aproximación del heap, no de la memoria total de la pila.
- El benchmark es educativo. Para microbenchmarks de producción se recomienda JMH.
- El límite de pila depende de `-Xss`, de la JVM y de la plataforma.

## Documentación

- Reporte técnico: `docs/reporte-tecnico.pdf`
- Referencias: `REFERENCES.md`

## Autores

- Diego Alejandro Reyes Cárdenas
- Jedric Julián Montealegre Contreras
- David Eduardo Calderón Parra

## Licencia

Este proyecto se distribuye bajo la licencia MIT. Consulte el archivo `LICENSE`.
