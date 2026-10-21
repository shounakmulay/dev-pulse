---
name: test-change
description: Use when adding or reviewing behavioral tests for DevPulse features, state/effects, use cases, network boundaries, paging, persistence or coroutine lifetimes.
---

# Test the requested behavior

Read the acceptance cases from [plan-change](../plan-change/SKILL.md) and the real implementation/callers. Choose the smallest boundary that can expose the bug or prove the requested behavior. Keep tests in the affected module and do not add tests for trivial reversible edits or assertions that only mirror implementation.

## Choose a real local example

- [SafeRunCatchingTest](../../../core/common/src/commonTest/kotlin/dev/shounakmulay/devpulse/core/common/coroutines/SafeRunCatchingTest.kt): real blocks prove single execution, preserved ordinary failures and propagated cancellation. Counters here measure a visible side effect; they are not a production-state pattern.
- [DevPulseNetworkClientTest](../../../core/network/src/commonTest/kotlin/dev/shounakmulay/devpulse/core/network/DevPulseNetworkClientTest.kt): MockEngine exercises real decoding/header/text wrappers. Keep the serializer and mapper real.
- [FeedDiscoveryDatabaseTest](../../../core/data/db/src/jvmTest/kotlin/dev/shounakmulay/devpulse/core/data/db/FeedDiscoveryDatabaseTest.kt): real Room assertions cover paging order, duplicate memberships, invalidation and populated reopen. Follow the current schema policy, not historical version numbers.
- [DiscoveryFeedInteractorTest](../../../feature/feed/src/androidHostTest/kotlin/dev/shounakmulay/devpulse/feature/feed/interactor/feed/DiscoveryFeedInteractorTest.kt): real use cases/interactor with fake repository/queue boundaries assert literal payloads, no duplicate enqueue, propagated failures/cancellation and switched/cancelled paging streams. Its ViewModel case controls virtual time, sets Main and Koin's SavedStateHandle, then cancels the ViewModel, stops Koin and resets Main in finally.

Do not copy ExampleUnitTest/ExampleInstrumentedTest arithmetic assertions or an empty event handler as feature coverage. Existing state-serialization tests do not prove UI interaction.

## Source sets and dependencies

Read the affected build.gradle.kts and convention plugins first. commonTest runs shared behavior; jvmTest is appropriate for JVM-specific Room/filesystem tests; androidHostTest covers Android/Robolectric behavior only where withHostTestBuilder is configured. androidDeviceTest requires an actual device lane.

Use kotlin.test, JUnit and coroutine-test APIs already wired in that module. Orbit test, Turbine or Mokkery availability varies by module; inspect dependencies before importing them. Do not introduce MockK or TestCoroutineDispatcher from obsolete templates. Both bounded Flow.first() and scheduled collectors occur in maintained tests; choose a method matching the lifetime being asserted instead of mandating a single library everywhere.

## Assertions and isolation

Name tests Given-When-Then. Assert literal outcomes the caller observes, including relevant negative cases. Keep mappers, domain models, SQL and utility logic real; fake external/repository boundaries only when that boundary is not the behavior under test. Avoid relaxed mocks that manufacture successful results.

For flows/search, prove old streams stop after selection/query changes, virtual debounce boundaries behave correctly, and cancelled jobs cannot emit stale results. For state/effects, call events and observe resulting state/effects through the current Orbit APIs. Establish a real subscription when the ViewModel starts work in bindStateSources/repeatOnSubscription.

Use runTest/testScheduler and bounded waits instead of wall-clock sleeps. Clean up Main replacements, Koin, ViewModels, databases, files and owned clients in finally. Tests touching global DI/Main/native/filesystem state run serially. Keep network/service tests isolated unless live access is explicitly part of the requested validation.

For SQL/persistence, use real Room queries and literal row IDs/order; include ties, scope changes and invalidation. For schema changes, follow [change-database](../change-database/SKILL.md); a successful query-string assertion or fresh database open does not prove a populated migration.

## Test both sides of the error boundary

- A repository/client/DAO fake returns plain values and throws a chosen exception; it must not manufacture Result.failure or catch the exception it is meant to expose. A repository test asserts that technical failure throws, while the use-case test asserts Result.failure preserves that failure. These are different contracts.
- A one-shot use case returns one Result<T>, including Result<Unit> for a command. Verify successful output, ordinary exception conversion, injected I/O/default dispatcher choice, and cancellation propagation. A withContext-only forwarding method is insufficient to establish exception conversion.
- An observed use case consumes a plain repository Flow<T> and emits Flow<Result<T>> through flowCachingOnDefault. Verify successful values, upstream exception conversion, construction-time failure when relevant, cancellation and downstream exception transparency. The helper has no explicit CancellationException branch; test the concrete cancellation path and report a helper defect rather than silently weakening the contract or adding a repository catch.
- When nesting use cases, assert the inner failure reaches the outer failure without Result<Result<T>> or a getOrNull()-induced success. Legitimate missing data, preference defaults and typed revision conflicts remain ordinary domain outcomes, not infrastructure failures.

Use the settings setter/observer pair linked from implement-change as the error-boundary blueprint. Some existing repository/read-state fixtures still use Result below the use case; adapt their boundary when that production contract is intentionally changed, rather than copying the violation. Do not alter application code or tests merely to refresh skill documentation.

## Run and interpret

```bash
bash scripts/verify.sh --test :core:common dev.shounakmulay.devpulse.core.common.coroutines.SafeRunCatchingTest
bash scripts/verify.sh --test :core:network dev.shounakmulay.devpulse.core.network.DevPulseNetworkClientTest
```

For the existing Android discovery suite, task help confirms the class selector and fail-fast option. Inspect its task graph first and use an isolated checkout if schema/config writers occur:

```bash
./gradlew --offline --no-daemon -Dorg.gradle.java.installations.auto-download=false :feature:feed:testAndroidHostTest --tests dev.shounakmulay.devpulse.feature.feed.interactor.feed.DiscoveryFeedInteractorTest --fail-fast
```

These commands select a class, not all affected consumers. [verify-change](../verify-change/SKILL.md) selects broader checks. Compilation selectors have module scope; no configured native related-test selector exists. Report NO-SOURCE or unavailable source sets as missing evidence, not passing tests. Do not fix baseline failures by deleting assertions, blessing snapshots or broadening suppressions.

Finish with UI/device interaction checks for visual, accessibility, navigation and native behavior. Report the actual command/result and missing platforms. Never infer runtime behavior from compilation or fixture tests alone.
