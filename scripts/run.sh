#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
"$ROOT/scripts/compile.sh"
cd "$ROOT"
java -cp out/main com.proyecto.recursivos.BenchmarkRunner "$@"
