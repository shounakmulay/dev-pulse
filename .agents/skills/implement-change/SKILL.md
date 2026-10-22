---
name: implement-change
description: Use when implementing or refactoring existing DevPulse behavior across screens, state, use cases, repositories or shared utilities after establishing acceptance checks.
---

# Implement a DevPulse change

Start with [plan-change](../plan-change/SKILL.md). Carry an authorized implementation through [test-change](../test-change/SKILL.md) and [verify-change](../verify-change/SKILL.md). A plan, scaffold or successful compile alone does not complete a feature.

## Establish the contract

Read the affected code, consumers and tests. State what the user will observe, which component owns the decision and which checks establish it. Use the smallest cohesive change; preserve unrelated dirty files and avoid speculative abstractions or dependencies.

## Required exception and dispatcher boundary

Repositories and every layer below use cases return plain values, nullable values for legitimate absence, Unit, Flow<T> or Flow<PagingData<T>>. Failures propagate as exceptions. Never use Kotlin Result, Result.success/failure, runCatching, safeRunCatching, try/catch, Flow.catch or error-to-default fallback in those lower layers. Cleanup in finally/use is still required. Lower-level calls to another use case would reverse this boundary; keep dependencies in their intended direction.

Use cases always expose Result<T> for one-shot work, including Result<Unit>, and Flow<Result<T>> for observed work. Catch ordinary exceptions through the existing DispatcherProvider helpers instead of adding ad hoc catches or using withContext alone. Helper definitions can live in core/common; their exception-converting call sites belong in use cases.

Choose runCachingOnIO for I/O work and runCatchingOnDefault for CPU/default-dispatcher work. Inject DispatcherProvider; do not hardcode Dispatchers or move dispatcher/error policy into repositories or ViewModels. These helper names are the current API spellings, including Caching in runCachingOnIO and flowCachingOnDefault.

For an I/O setter use case, the required shape is:

```kotlin
suspend operator fun invoke(enabled: Boolean): Result<Unit> =
    dispatcherProvider.runCachingOnIO {
        feedSettingsRepository.setSyncInBackground(enabled)
    }
```

For a separate observation use case, follow [ObserveSyncInBackgroundUseCase](../../../core/domain/settings/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/domain/settings/feed/ObserveSyncInBackgroundUseCase.kt):

```kotlin
operator fun invoke(defaultValue: Boolean = true): Flow<Result<Boolean>> =
    feedSettingsRepository.observeSyncInBackground(defaultValue)
        .flowCachingOnDefault(dispatcherProvider)
```

[FeedSettingsRepositoryImpl](../../../core/data/settings/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/data/settings/FeedSettingsRepositoryImpl.kt) supplies plain Flow<Boolean>/Unit, maps a missing preference to the supplied domain default and uses distinctUntilChanged. This normal default is not an exception fallback. Keep the @Factory constructor-injected operator invoke pattern used by the linked use cases.

Apply flowCachingOnDefault once at the use-case boundary after building the complete upstream operation. If creating the upstream flow can throw before collection, defer its construction inside a Flow builder so that failure also reaches this boundary. Do not return Result<Flow<T>>, Flow<Result<Result<T>>> or introduce a Flow.catch in the repository. Preserve cancellation and upstream/downstream exception transparency; exercise these cases rather than assuming a helper covers them automatically.

When one use case calls another, unwrap a failing inner Result with getOrThrow() inside the outer dispatcher-catching block to avoid nested Result and accidental success. Use getOrNull()/defaults only for an explicit domain fallback, never to silently erase a required failure. Callers above use cases handle the returned Result deliberately.

[SetPostReadUseCase](../../../core/domain/feed/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/domain/feed/posts/SetPostReadUseCase.kt) exposes Result<Unit> over a plain repository setter. Domain outcome types, when needed, are plain values rather than Kotlin's exception wrapper. Do not turn legitimate absence into a technical exception. Existing repository Result-returning methods and withContext-only forwarding use cases are nonconforming examples, not blueprints.

## Match existing action patterns

Start from a neighboring action in the same feature. Keep its event -> ViewModel intent -> use-case path. When several ViewModels share action behavior, put the shared method in the existing feature interactor rather than introducing a separate action processor. Keep use-case error boundaries and handle the returned Result deliberately in the owning caller or interactor.

For example, [PostInteractor](../../../feature/feed/src/commonMain/kotlin/dev/shounakmulay/devpulse/feature/feed/interactor/post/PostInteractor.kt) owns the shared read/unread setter. Its ViewModels call that method from their existing intents; database observation updates the post appearance. Do not wrap a boolean setter in a dedicated processor, mutex, pending-ID StateFlow, command/outcome hierarchy, revision protocol, Undo or snackbar flow unless explicitly requested or required by a concrete observed behavior. Reuse the established UI and processing paths instead of adding speculative protection against hypothetical races. Actual queued background work still follows its existing queue ownership.

When simplifying, delete the superseded class, DI dependency, transient state, UI parameters and tests that only assert removed machinery. Retain tests for persisted behavior, failure handling and cancellation at the surviving boundary.

## Inline screen event callbacks

Write simple event and navigation lambdas directly in the child composable's named argument. Do not declare a local `val onReadChanged`, `val onBookmarkChanged` or similar callback alias only to pass it once. Keep event dispatch visible at the call site and let Kotlin infer callback parameter types. Do not add `remember` or a callback-holder type merely to wrap event dispatch. Normal callback parameters passed through reusable components remain appropriate.

Prefer this small call-site snippet:

```kotlin
onReadChanged = { id, isRead ->
    viewModel.onEvent(FeedScreenEvent.SetPostRead(id, isRead))
},
```

Avoid this extra indirection:

```kotlin
val onReadChanged: (UUID, Boolean) -> Unit = { id, isRead ->
    viewModel.onEvent(FeedScreenEvent.SetPostRead(id, isRead))
}
PostsList(onReadChanged = onReadChanged)
```

## Presentation and domain

- Use [MviViewModel<STATE, EFFECT>](../../../core/ui/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/ui/viewmodel/MviViewModel.kt) with an initial state and createStateSerializer(). ScreenState is immutable/serializable; effects implement Effect; eventful ViewModels implement EventHandler and use @KoinViewModel.
- State reduction uses setState and copy; one-time effects use postEffect/Orbit collection. Match the existing screen's direct collection and event handling. Do not introduce the obsolete StateEffectViewModel, setEffect, createInitialState or a mandatory Connector/Content split.
- For controlled text input, inspect [FeedListViewModel.onSearchQueryChanged](../../../feature/feed/src/commonMain/kotlin/dev/shounakmulay/devpulse/feature/feed/screens/feedlist/ui/FeedListViewModel.kt): blockingSetState updates the query synchronously. Keep input feedback immediate and debounce the expensive search work. Do not apply blocking reductions to arbitrary long-running work.
- Keep PagingData in the established stream and cachedIn(viewModelScope), as in FeedListViewModel.uiFeedsFlow. Persisted changes should reach the UI through repository/DAO observation and invalidation. Do not add manual refreshes or a second in-memory result store without a demonstrated need.
- Delegate business decisions to existing use cases; feature-local interactors shape UI data. [SetSyncInBackgroundUseCase.invoke](../../../core/domain/settings/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/domain/settings/feed/SetSyncInBackgroundUseCase.kt) demonstrates the use-case-owned Result boundary and injected DispatcherProvider. Existing repository Result/catch implementations violate the required convention; inspect callers when correcting a touched contract, rather than preserving or spreading the violation.
- Handle failures deliberately in the state/effect/caller that owns recovery. The settings sync toggle and feed pin caller currently ignore some failures; do not copy that omission into new behavior.

## Storage, work and lifetimes

- Follow [change-database](../change-database/SKILL.md) for storage/query changes, [change-network](../change-network/SKILL.md) for remote data and [change-platform](../change-platform/SKILL.md) for native/DI/background work.
- CONTEXT.md assigns publisher, user-owned and derived article fields separately. A feed sync must preserve user decisions and stable local identity. Trace the actual mapper and write statement before changing reconciliation.
- Enqueue and observe long-running feed processing through the existing queue path. Do not start another queue loop in a ViewModel. Distinguish accepted/enqueued work from completed work, and inspect the current executor's owner and concurrency controls.
- Use viewModelScope or subscription scopes for screen work, and the established application/service/worker owner for longer work. The suspend helpers in [DispatcherProvider](../../../core/common/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/common/coroutines/DispatcherProvider.kt) rethrow CancellationException. Invoke exception-to-Result helpers only at a use-case boundary; cancellation must not become success, ordinary failure or retry.
- Keep native APIs in configured platform source sets and update all actual implementations affected by a shared contract. Use current annotated Koin wiring at the application root.

## UI and source integrity

Use DP widgets and DPTheme tokens. Ask before substituting raw Material3 when the design system lacks a component. Keep user-facing text in existing resource/TextResource paths, not implementation details. Use stable item identity and preserve loading, empty, failure and populated states. Inspect [FeedPreviewData](../../../feature/feed/src/commonMain/kotlin/dev/shounakmulay/devpulse/feature/feed/preview/FeedPreviewData.kt) for representative preview values; do not copy fake IDs into production behavior. Render changed visual states and check labels, focus, font scaling and narrow/wide layouts where relevant.

Use Modules constants and existing convention plugins. Re-audit scripts/verification.json if build configuration changes. Do not add casts, suppressions, ignored errors, comments narrating code, dependency upgrades or unrelated cleanup to force a pass.

## Finish

Exercise the acceptance cases with the real owners and consumers. Preview `bash scripts/verify.sh --base <reviewed-integration-ref> --plan`, execute relevant checks, and review the final diff against the selected examples. Report exact checks and runtime/platform gaps. Do not claim improvement in performance without a relevant measurement.
