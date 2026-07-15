#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
"$ROOT/scripts/compile.sh"
cd "$ROOT"
java -ea -cp "out/main:out/test" com.proyecto.recursivos.AlgorithmTests
