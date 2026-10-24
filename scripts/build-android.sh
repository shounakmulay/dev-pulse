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
./gradlew ":androidApp:${task}${flavor}${variant}" "-Pbuildkonfig.flavor=$environment"

case "$format" in
    apk) output_dir="$root/androidApp/build/outputs/apk/$environment/$build_type" ;;
    aab) output_dir="$root/androidApp/build/outputs/bundle/${environment}${variant}" ;;
esac
shopt -s nullglob
artifacts=("$output_dir"/*."$format")
if [[ ${#artifacts[@]} -eq 0 ]]; then
    echo "Build finished but no .$format file was found in $output_dir" >&2
    exit 1
fi
printf '\nBuild output:\n'
printf '%s\n' "${artifacts[@]}"
