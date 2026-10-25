#!/usr/bin/env bash
set -euo pipefail
python3 -B .github/scripts/test_android_cd.py
python3 -B .github/scripts/test_play_auth.py
python3 -B scripts/test_quality_hooks.py
python3 -B scripts/test_verify.py
python3 -B scripts/test_build_android.py
./gradlew qualityCheck --continue --console=plain
