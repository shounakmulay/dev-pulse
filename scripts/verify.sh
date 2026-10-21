#!/usr/bin/env bash
set -euo pipefail
root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
command -v python3 >/dev/null || { echo 'FAIL: Python 3.7+ is required; no tools were installed.' >&2; exit 1; }
exec python3 "$root/scripts/verify.py" "$@"
