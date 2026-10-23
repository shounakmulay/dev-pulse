#!/usr/bin/env sh
set -eu
root=$(git rev-parse --show-toplevel)
cd "$root"
hook=$(git rev-parse --git-path hooks/pre-push)
if [ -e "$hook" ] || [ -L "$hook" ]; then
    if cmp -s .githooks/pre-push "$hook"; then
        chmod +x "$hook"
        printf '%s\n' 'DevPulse pre-push hook is already installed.'
        exit 0
    fi
    printf '%s\n' "Refusing to replace existing hook at $hook. Integrate .githooks/pre-push into your hook manually." >&2
    exit 1
fi
mkdir -p "$(dirname "$hook")"
(set -C; cat .githooks/pre-push > "$hook")
chmod +x "$hook"
printf '%s\n' "Installed DevPulse pre-push hook at $hook."
