#!/usr/bin/env bash
set -euo pipefail

environment="${1:-staging}"
build_type="${2:-debug}"
format="${3:-apk}"

if [[ $# -gt 3 ]]; then
    echo 'Usage: build-android.sh [staging|production] [debug|release] [apk|aab]' >&2
    exit 1
fi

case "$environment" in
    staging) flavor=Staging ;;
    production) flavor=Production ;;
    *) echo "Unknown environment: $environment" >&2; exit 1 ;;
esac
case "$build_type" in
    debug) variant=Debug ;;
    release) variant=Release ;;
    *) echo "Unknown build type: $build_type" >&2; exit 1 ;;
esac
case "$format" in
    apk) task=assemble ;;
    aab) task=bundle ;;
    *) echo "Unknown format: $format" >&2; exit 1 ;;
esac

root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$root"
exec ./gradlew ":androidApp:${task}${flavor}${variant}" "-Pbuildkonfig.flavor=$environment"
