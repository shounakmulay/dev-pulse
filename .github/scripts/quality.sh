#!/usr/bin/env bash
set -euo pipefail
python3 -B scripts/test_quality_hooks.py
python3 -B scripts/test_verify.py
./gradlew qualityCheck --continue --console=plain
