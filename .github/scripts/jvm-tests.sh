#!/usr/bin/env bash
set -euo pipefail
./gradlew \
  :core:data:db:jvmTest \
  :core:data:feed:jvmTest \
  :core:network:jvmTest \
  --continue --console=plain "$@"
