---
name: verify-change
description: Use when verifying DevPulse changes, choosing scoped Gradle checks, reviewing database or feed changes, or reporting platform verification gaps.
---

# Verify a DevPulse change

Read applicable AGENTS.md instructions, CONTEXT.md, changed code, callers and behavioral tests. Preserve existing worktree changes. The root schema baseline policy takes precedence over historical migration examples.

## Plan

State the observable behavior, owner, edge cases and acceptance checks. Distinguish intended domain behavior in CONTEXT.md from implemented behavior. Use Modules.kt constants for dependencies and buildSrc convention plugins. Inspect current source before copying a template.

Run from any directory:

```bash
bash /path/to/DevPulse/scripts/verify.sh --base <reviewed-integration-ref> --plan
```

Choose the integration ref explicitly. Tracking branches do not prove the PR target. Without an explicit ref or a verified remote default branch, the runner selects all modules. It combines branch, staged, unstaged and untracked changes, then adds dependent consumers using an audited graph. Build configuration drift fails until the graph and task inventory are reviewed and refreshed in scripts/verification.json.

## Choose the relevant workflow

Use [plan-change](../plan-change/SKILL.md), [implement-change](../implement-change/SKILL.md) and [test-change](../test-change/SKILL.md) for requirements, implementation and behavioral assertions. Load the database/network/platform specialists when those boundaries change. This skill owns check selection and completion evidence.

## Review before selecting checks

Read the acceptance cases and specialist workflow for the affected boundary. Confirm whether changes involve controlled input/state timing, database/query/schema behavior, HTTP decoding/cache behavior, native startup/lifecycle, or visual/navigation behavior. Select checks that exercise those owners and their consumers. Preserve tests and constraints; never weaken them to obtain a pass.

## Validate

The runner needs installed Python 3.7+, Bash, Git, a cached Gradle 9.3.1 distribution, a compatible JDK and cached dependencies. It installs nothing. Android needs SDK 37; iOS compilation needs macOS, Xcode and cached Kotlin/Native dependencies. The catalog's JVM target 11 is bytecode compatibility, not a Gradle runtime pin.

```bash
bash scripts/verify.sh --base <reviewed-integration-ref>
bash scripts/verify.sh --all --plan
bash scripts/verify.sh --all --strict
DEVPULSE_VERIFY_ANDROID=1 DEVPULSE_VERIFY_IOS=1 bash scripts/verify.sh --all
bash scripts/verify.sh --test :core:common dev.shounakmulay.devpulse.core.common.coroutines.SafeRunCatchingTest
```

`--test MODULE CLASS` checks only that class and does not verify consumers. Gradle supports class patterns via `--tests`; compilation has module scope. No file-level compiler selector or native related-test selector is configured. Do not invent one.

Normal runs perform registry/reference validation, runner fixtures, executable task discovery, task-graph preflight and selected JVM lint/compilation/tests. Android and iOS are explicit opt-ins. Tests and suites run serially. Gradle offline mode prevents dependency resolution downloads; it does not isolate arbitrary test networking. Review newly introduced service-backed tests before running them.

Tracked generated outputs must remain unchanged. The runner refuses known artifact-writing task graphs instead of excluding generators silently. Use an isolated checkout to verify those graphs. No device, emulator, signing or release tasks run automatically.

Strict mode fails on omitted applicable checks. Manual UI/device checks remain outside this runner, so a full success is unavailable while those checks apply. Visual or navigation changes need interaction checks, restoration/back behavior and accessibility review on affected layouts. Preview compilation does not prove canvas rendering or runtime correctness.

## Blueprints

Paths below are relative to the repository root and are checked by scripts/verification.json.

- `core/common/src/commonTest/kotlin/dev/shounakmulay/devpulse/core/common/coroutines/SafeRunCatchingTest.kt`, `SafeRunCatchingTest`: observable helper behavior with real blocks and literal expectations. Do not copy test-local mutable counters into production state.
- `core/network/src/commonTest/kotlin/dev/shounakmulay/devpulse/core/network/DevPulseNetworkClientTest.kt`, `DevPulseNetworkClientTest`: real response decoding through MockEngine, mixed-case headers and generic bodies. It does not prove live HTTP cache behavior after process restart.
- `core/data/db/src/jvmTest/kotlin/dev/shounakmulay/devpulse/core/data/db/FeedDiscoveryDatabaseTest.kt`, `FeedDiscoveryDatabaseTest`: real Room paging, invalidation and populated database reopen checks. Its current baseline reopen assertions check version 1; inspect the active schema and do not restore the retired v1/v2/v3 migration lineage.

## Completion

Review the final diff and existing dirty files. Report exact commands, passed checks, baseline failures, disabled stages and unavailable platforms. Separate proposed, compiled and runtime-verified behavior. A task listing proves task availability only; whitespace checks prove whitespace only. No CI or hook integration is implied by this runner.

## Error-contract review

Before completion, inspect changed signatures and call sites: no Kotlin Result, try/catch, runCatching, safeRunCatching, Flow.catch or exception-to-default recovery below use cases. Use cases return Result<T>/Flow<Result<T>> through the existing DispatcherProvider helpers, with no nested Result or Result<Flow<T>>. Confirm ordinary failures are converted once, cancellation propagates, and callers above the use case handle failure deliberately. Read named domain outcome types before treating a class whose name ends in Result as Kotlin Result. This is a semantic review requirement, not a claim that the runner mechanically enforces layer boundaries.
