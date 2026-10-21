---
name: add-feature
description: Use when adding a new DevPulse KMP feature module, tab or top-level feature and wiring its build, DI and navigation.
---

# Add a feature

Read AGENTS.md, CONTEXT.md, settings.gradle.kts, Modules.kt and the current feature/settings build and DI entry points. Define the feature boundary, route, observable behavior and acceptance checks. Reuse installed dependencies and avoid unrelated module cleanup.

1. Add a Modules.Feature constant in `buildSrc/src/main/kotlin/dev/shounakmulay/devpulse/buildsrc/constants/Modules.kt` and include the module in settings.gradle.kts.
2. Use the current devpulse.kmp.library.compose convention plugin and inspect feature/settings/build.gradle.kts for target/source-set configuration. Enable only the host/device test targets actually needed; do not use assemble as a default verification task because it can invoke native/link tasks and tracked config generation.
3. Use package and directory root `dev/shounakmulay/devpulse/feature/<name>`. Add an annotated Koin module scanning that package and register it at the existing application composition root.
4. Follow `.agents/skills/add-screen/SKILL.md` for initial screen/state/effect/event/ViewModel and typed navigation. Use current code, not legacy StateEffectViewModel templates.
5. Inspect current app-level entry-provider and tab registration before wiring the new feature. Add dependencies with Modules constants. Check tab selection/reselection, back behavior and restoration.
6. Add behavioral tests where meaningful. The home module's empty event handler and example arithmetic tests are not production blueprints.
7. Re-audit module dependencies, tasks and fingerprints in scripts/verification.json after changing build configuration. Follow `.agents/skills/verify-change/SKILL.md`, run `bash scripts/verify.sh --all --plan`, then the relevant checks. Report native/device gaps honestly.

Complete only when build/DI/navigation registrations and user-visible behavior are verified to the stated scope. Do not add dependencies or disable checks merely to pass compilation.

Current app navigation ownership: [DevPulseNavApp](../../../composeApp/src/commonMain/kotlin/dev/shounakmulay/devpulse/composables/DevPulseNavApp.kt) defines tabRoutes, [DevPulseNavSuiteScaffold](../../../composeApp/src/commonMain/kotlin/dev/shounakmulay/devpulse/composables/DevPulseNavSuiteScaffold.kt) renders tab icons/reselection, and [DevPulseNavDisplay](../../../composeApp/src/commonMain/kotlin/dev/shounakmulay/devpulse/composables/DevPulseNavDisplay.kt) composes feature entry providers. [FeedFeatureEntries](../../../feature/feed/src/commonMain/kotlin/dev/shounakmulay/devpulse/feature/feed/navigation/FeedFeatureEntries.kt) aggregates per-screen entries. Update the relevant owner instead of copying historical App.kt registration snippets. The current empty tab-icon contentDescription is not an accessibility blueprint.

For every new data operation, keep the error boundary in its use case: one-shot work returns Result<T>/Result<Unit> through the injected dispatcher-catching helper, and observation returns Flow<Result<T>> through flowCachingOnDefault. Repositories and all lower layers expose plain T/Unit/Flow<T>, propagate exceptions and never catch or wrap them. Handle the use-case Result in the screen/interactor; do not place technical exception recovery in a ViewModel. Follow implement-change for the exact helper names, injected dispatcher choice and nested-use-case rules.
