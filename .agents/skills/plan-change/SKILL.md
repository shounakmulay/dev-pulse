---
name: plan-change
description: Use when planning a DevPulse feature, bug fix or refactor, clarifying ownership and edge cases, or deciding the implementation and acceptance checks.
---

# Plan a DevPulse change

Use before changing behavior. For a small fix, keep the plan in the conversation; do not create a planning document just to follow this skill. Planning must lead into implementation and testing when the user requested implementation.

## Inspect and decide

1. Read AGENTS.md, CONTEXT.md, applicable scoped instructions and the current worktree status. Preserve unrelated edits. Read an existing task/spec if supplied; an absent spec is a gap, not permission to invent its contents.
2. If .codegraph exists, use codegraph_explore with exact paths or symbols before scoped text searches. Trace the affected entry point, consumers, data boundary and tests. Exclude generated output and caches.
3. Separate existing behavior, intended domain behavior and the requested change. CONTEXT.md describes feed ownership and processing intent; it is not proof that every behavior is implemented. Read active plans only when the requested work touches them.
4. Describe the user action and expected outcome, exact classes/functions that will change, state/data ownership, lifetime and failure behavior. Prefer extending the current path over adding a second refresh, paging or processing path. Inspect a neighboring action before proposing new machinery. Reuse its event, intent and use-case pattern; place behavior shared by multiple ViewModels in the existing feature interactor. Name the concrete requirement before adding an action processor, command hierarchy, pending-operation registry or synchronization.
5. Identify schema, serialized-state, background-work and platform compatibility implications. Route to specialist skills below. Ask only for decisions the task and available evidence cannot resolve; continue independent authorized work.
6. Define acceptance cases before implementation. Include relevant success, invalid input, failure, cancellation, stale/concurrent work, empty data, restoration and offline/reopen cases. Do not mechanically require every category for every change.

## Ownership references

- [Modules](../../../buildSrc/src/main/kotlin/dev/shounakmulay/devpulse/buildsrc/constants/Modules.kt) and settings.gradle.kts identify actual modules. Presentation depends on domain use cases; current repository interfaces live in data modules. Feature-local interactors map domain streams into UI models. Do not relocate interfaces to fit generic Clean Architecture examples.
- [SetSyncInBackgroundUseCase](../../../core/domain/settings/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/domain/settings/feed/SetSyncInBackgroundUseCase.kt) calls [FeedSettingsRepositoryImpl](../../../core/data/settings/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/settings/FeedSettingsRepositoryImpl.kt) and [DatastoreDevPulsePreferences](../../../core/data/preferences/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/preferences/DatastoreDevPulsePreferences.kt). This is a useful settings persistence path; it is not evidence for a new database table or a second preference store.
- [FeedListViewModel](../../../feature/feed/src/commonMain/kotlin/dev/shounakmulay/devpulse/feature/feed/screens/feedlist/ui/FeedListViewModel.kt) shows the state -> interactor -> cached paging stream relationship. Its ignored pin-write result is a limitation, not error-handling guidance.
- [FeedDiscoveryDatabaseTest](../../../core/data/db/src/jvmTest/kotlin/dev/shounakmulay/devpulse/core/data/db/FeedDiscoveryDatabaseTest.kt) checks literal results through real storage. Use that kind of acceptance evidence for database behavior, with the current baseline policy.

## Error-contract acceptance

Plan the error boundary explicitly: repositories, DAOs, clients, parsers, mappers and other operations below use cases return plain T, Unit or Flow<T> and let failures throw. Only use cases convert ordinary exceptions to Result<T>/Flow<Result<T>> with the injected DispatcherProvider helpers. Identify whether work is I/O, CPU work or Flow collection, and how callers above the use case handle the Result. Treat existing lower-layer Result/catch implementations as violations to correct within the requested scope, not alternative conventions. Acceptance cases distinguish lower-layer thrown failure, use-case failure conversion, upstream Flow failure and propagated cancellation.

## Route by the actual change

- Existing behavior: [implement-change](../implement-change/SKILL.md); new screen/module: [add-screen](../add-screen/SKILL.md) or [add-feature](../add-feature/SKILL.md).
- Routes, tab stacks or panes: [navigation-3](../navigation-3/SKILL.md); database/query/persistence: [change-database](../change-database/SKILL.md).
- HTTP, decoding or catalog reconciliation: [change-network](../change-network/SKILL.md); startup, DI or native/background work: [change-platform](../change-platform/SKILL.md).
- Behavioral assertions: [test-change](../test-change/SKILL.md); execution scope and final evidence: [verify-change](../verify-change/SKILL.md).

## Plan output and completion

State the observable change, exact owners, cohesive file set, edge cases, smallest useful tests and platform/runtime checks. Name any baseline failure or unresolved decision. Respect an explicit preview-only request. Once authorized, continue with implement-change instead of stopping after a plan.

Preview verification using `bash scripts/verify.sh --base <reviewed-integration-ref> --plan`. This command plans checks; it does not validate the feature. Reuse existing canonical guidance on refresh instead of adding duplicate skills.
