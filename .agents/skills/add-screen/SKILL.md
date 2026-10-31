---
name: add-screen
description: Use when adding a screen to an existing DevPulse feature module, including state, events, effects, ViewModel and navigation wiring.
---

# Add a screen

Read AGENTS.md, the feature build file, its navigation entry point, and the current sibling screen's code and behavioral tests. State the behavior, owning use cases and acceptance checks before implementation.

Use the package root `dev.shounakmulay.devpulse`. Put screen files under `feature/<feature>/src/commonMain/kotlin/dev/shounakmulay/devpulse/feature/<feature>/screens/<screen>/`.

Inspect these current references instead of copying an embedded template:

- `core/ui/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/ui/viewmodel/MviViewModel.kt`: state/effect generic parameters, initial state and serializer hook.
- `feature/settings/src/commonMain/kotlin/dev/shounakmulay/devpulse/feature/settings/screens/settings/ui/SettingsViewModel.kt`: EventHandler and lifecycle-bound observation. Its sync toggle currently ignores the returned failure; do not copy that omission.
- `feature/feed/src/commonMain/kotlin/dev/shounakmulay/devpulse/feature/feed/screens/feed/navigation/FeedScreenEntry.kt`: typed entry and Koin ViewModel injection.
- `core/navigation/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/navigation/Screen.kt`: actual serializable route hierarchy and parameter types.

Create immutable serializable ScreenState, Effect and Event types as needed. Use MviViewModel<STATE, EFFECT>, @KoinViewModel, createStateSerializer(), Orbit state/effect APIs and DP components. Do not use StateEffectViewModel or createInitialState().

Add the typed route, screen entry and feature-entry registration. Register the new back-stack route as a `NavKey` subclass in `navigationSerializersConfig` in `core/navigation/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/navigation/NavigationState.kt`; `@Serializable` on the route alone is not sufficient. Add a serialization and restoration regression test using that configuration, including any route parameters. Inspect the current app entry provider before wiring it. Follow current list/detail metadata when applicable. Keep business decisions in use cases and preserve cancellation and state restoration.

Read `.agents/skills/verify-change/SKILL.md`. Preview `bash scripts/verify.sh --base <reviewed-integration-ref> --plan`, then execute it. New behavior needs meaningful assertions; existing placeholder tests are not a quality target. Check narrow/wide layout, back navigation, state restoration and relevant accessibility manually. Report unavailable platforms explicitly.

Current app navigation ownership: [DevPulseNavApp](../../../composeApp/src/commonMain/kotlin/dev/shounakmulay/devpulse/composables/DevPulseNavApp.kt) defines tabRoutes, [DevPulseNavSuiteScaffold](../../../composeApp/src/commonMain/kotlin/dev/shounakmulay/devpulse/composables/DevPulseNavSuiteScaffold.kt) renders tab icons/reselection, and [DevPulseNavDisplay](../../../composeApp/src/commonMain/kotlin/dev/shounakmulay/devpulse/composables/DevPulseNavDisplay.kt) composes feature entry providers. [FeedFeatureEntries](../../../feature/feed/src/commonMain/kotlin/dev/shounakmulay/devpulse/feature/feed/navigation/FeedFeatureEntries.kt) aggregates per-screen entries. Update the relevant owner instead of copying historical App.kt registration snippets. The current empty tab-icon contentDescription is not an accessibility blueprint.

For every new data operation, keep the error boundary in its use case: one-shot work returns Result<T>/Result<Unit> through the injected dispatcher-catching helper, and observation returns Flow<Result<T>> through flowCachingOnDefault. Repositories and all lower layers expose plain T/Unit/Flow<T>, propagate exceptions and never catch or wrap them. Handle the use-case Result in the screen/interactor; do not place technical exception recovery in a ViewModel. Follow implement-change for the exact helper names, injected dispatcher choice and nested-use-case rules.
