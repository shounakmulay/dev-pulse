# DevPulse agent guidance

DevPulse is a Kotlin Multiplatform app for Android, iOS and Desktop. Read applicable scoped guidance, current code, callers and behavioral tests before editing. Use CONTEXT.md for domain vocabulary and distinguish intended behavior there from implemented behavior.

## Implementation workflow

For behavior changes, read plan-change before editing, implement-change while implementing, then test-change and verify-change before reporting completion. Scale the work to the request. An authorized implementation continues through validation; a planning-only or preview-only request stops at that boundary.

Read the relevant canonical skill under `.agents/skills/<name>/SKILL.md`:

| Skill | Applies when |
|---|---|
| plan-change | Requirements, ownership, edge cases and acceptance checks |
| implement-change | Existing behavior, MVI state/effects, domain/data changes and final review |
| add-screen / add-feature | New screen or KMP module with build/DI/navigation wiring |
| navigation-3 | Routes, tab stacks, list/detail panes and restoration |
| change-database | Room schema, migrations, FTS, transactions, SQL, paging or persisted user state |
| change-network | HTTP, typed decoding, parsing and catalog reconciliation |
| change-platform | Startup, Koin, expect/actual, workers, services, notifications or permissions |
| test-change | Behavioral tests, source sets, coroutine/global-state isolation and runtime acceptance |
| verify-change | Scoped commands, drift checks and honest completion evidence |

- Preserve pre-existing worktree changes. Keep each change cohesive and avoid unrelated cleanup.
- State the intended observable behavior and concrete acceptance checks. Wait for confirmation on non-trivial implementation plans unless the current request already authorizes them.
- Reuse established mechanisms and dependencies. Never weaken checks, remove relevant tests, widen suppressions or add unsupported casts to obtain a pass.
- Read `.agents/skills/verify-change/SKILL.md` when verifying changes or selecting Gradle checks. Its reference tests cover cancellation, transport boundaries and real Room paging.
- Preview scope with `bash scripts/verify.sh --base <reviewed-integration-ref> --plan`; omit `--plan` to execute. Without a verified base, selection broadens to all modules.
- `bash scripts/verify.sh --test :core:common dev.shounakmulay.devpulse.core.common.coroutines.SafeRunCatchingTest` runs one class; it does not verify consumers.
- `--all` selects every module. `--strict` fails on omitted applicable checks. Android and iOS checks require `DEVPULSE_VERIFY_ANDROID=1` and `DEVPULSE_VERIFY_IOS=1` respectively. Device/UI verification remains manual.
- The runner uses the local cached toolchain, runs offline, and installs nothing. Build configuration changes require re-auditing `scripts/verification.json`; do not merely bless changed digests.
- Use `./gradlew qualityQuick` for Spotless/Detekt and `./gradlew qualityCheck` for production Android lint and JVM compilation. Kotlin test source sets are excluded by default; explicit `scripts/verify.sh --test MODULE CLASS` opts in to a JVM class. Run `./gradlew spotlessApply` only when formatting is intended. `ktlintCheck` and `ktlintFormat` do not exist. No architecture or dead-code gate is configured.
- Report executed checks, baseline failures and missing platform verification. Compilation, whitespace checks, preview rendering and runtime interaction are distinct evidence.

## Ownership and standards

- Follow Presentation -> Domain -> Data. Shared domain types live in `core/domain/models`; repository interfaces currently live in data modules. Do not move boundaries incidentally or add direct feature-to-feature dependencies.
- Use `Modules.kt` constants in `buildSrc/src/main/kotlin/dev/shounakmulay/devpulse/buildsrc/constants/` for project dependencies. Shared plugin and target configuration lives in `buildSrc`.
- Use `core/ui/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/ui/viewmodel/MviViewModel.kt`, Orbit state/effects, EventHandler and `@KoinViewModel`. Screen states are immutable and serializable. Use cases own business decisions; screen coroutines must respect lifecycle cancellation.
- Use annotated Koin modules. Compose the application graph at `composeApp/src/commonMain/kotlin/dev/shounakmulay/devpulse/di/DevPulseKoinApplication.kt`.
- Use DP-prefixed components from `core/designsystem`. If a required component is missing, ask before using raw Material3 primitives. Tokens come from DPTheme. Read spacing through `LocalDPSpacing.current`; do not access `DPTheme.spacing`, instantiate `DPSpacing`, or hardcode spacing values in UI code.
- Routes live in `core/navigation/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/navigation/Screen.kt` and are serializable NavKey types. Inspect current Navigator and entry metadata before changing list/detail behavior; do not copy stale route samples.
- Keep Ktor types inside `core/network`. Feed persistence, transactions, paging, FTS and exported schemas live in `core/data/db`; repository behavior lives in `core/data/feed`.
- New shared code belongs in commonMain; use platform source sets for native APIs and expect/actual only where needed. Source-set and test availability come from build configuration.

- Prefer val and immutable state updated with copy. Do not use Any. Use named arguments for 3+ parameters or non-obvious booleans; prefer when for sealed types or 3+ conditions.
- Use cases alone wrap ordinary failures in Kotlin Result or Flow<Result<T>> using DispatcherProvider.runCachingOnIO/runCatchingOnDefault or flowCachingOnDefault. Repositories and all lower layers return plain values/Unit/Flow<T>, throw on failure and never catch or construct Result. Preserve cancellation. Mock external boundaries only; use Given-When-Then tests with observable assertions.
- Do not add comments except for non-obvious math, unintuitive platform workarounds or intentional deviations that resemble bugs. Do not create Markdown files explaining code changes.
- Read only relevant files. Do not explore build/, .gradle/ or generated output. Use CodeGraph first when `.codegraph/` exists; otherwise use scoped search. Do not initialize an index.
- For commit workflows, use a descriptive feature branch; never commit directly on `main` or use the `codex/` branch prefix. Ticket numbers are optional; proceed without asking for one when none is supplied. Do not commit or push as part of setup unless requested.

## Database schema policy

The entity/user-state redesign for read and unread posts establishes a one-time new BASE = 1. Finish that feature's schema, generate its version 1 export, and retire the previous development migration lineage. No legacy migration or backfill is required for this reset.

After that baseline is established, every schema change requires a version increment, a new exported schema, and a registered, tested migration. Keep established schema exports immutable. This includes indexes, foreign keys, FTS configuration and custom triggers even when Room annotations do not capture them. Do not reset the baseline again or use destructive migration fallback. Normal data writes within an unchanged schema do not require a migration.

When the user invokes /poteto-mode, asks for poteto's style, or the task needs rigorous engineering playbooks, read the poteto-mode SKILL.md in full including its Principles index. Match its playbook and follow its autonomy and writing guidance. Resume the poteto-agent custom agent for poteto-mode work rather than spawning a generic sibling.
