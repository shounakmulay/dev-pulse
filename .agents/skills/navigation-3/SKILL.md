---
name: navigation-3
description: Use when changing DevPulse Navigation3 routes, tab stacks, adaptive list/detail metadata or back behavior.
---

# Change Navigation3

Read AGENTS.md, the affected feature's navigation entries and behavioral tests. State acceptance checks for tab selection, reselection, back, narrow/wide layouts and restoration.

Inspect current canonical implementation files under `core/navigation/src/commonMain/kotlin/dev/shounakmulay/devpulse/core/navigation/`:

- `Screen.kt` owns actual serializable NavKey types and parameter types.
- `Navigator.kt` owns navigation mutations and duplicate/tab handling. Its methods return Boolean results; inspect the relevant branch instead of assuming every route is pushed.
- `NavigationState.kt` owns root/tab stacks, selected-tab state and `navigationSerializersConfig` for polymorphic `NavKey` serialization.
- `NavDisplay.kt` and `scene/listDetail/ExpandableListDetailSceneStrategy.kt` own scene selection and pane metadata. Inspect current entry extensions before assigning pane roles.

Inspect `feature/feed/src/commonMain/kotlin/dev/shounakmulay/devpulse/feature/feed/screens/feed/navigation/FeedScreenEntry.kt` for typed entry/Koin wiring. It currently has no list-pane metadata; do not invent it from an old sample. Locate app entry-provider consumers through CodeGraph before adding a route.

Use typed serializable routes, not strings. Match actual parameter types. Register every new back-stack route as a `NavKey` subclass in `navigationSerializersConfig`; `@Serializable` alone does not register it. Add a regression test that serializes and restores a stack containing the route and its parameters using that configuration. Preserve tab stacks, root navigation and process restoration. Keep screen business decisions in use cases. Do not copy historical FeedDetail(id: Int) examples without reading current Screen.kt.

Follow `.agents/skills/verify-change/SKILL.md`. Preview `bash scripts/verify.sh --base <reviewed-integration-ref> --plan` and execute affected checks. Navigation currently has placeholder host/device tests; compilation alone cannot prove behavior. Perform back/reselection/resize/restoration interactions and report unavailable runtime verification.

Current app navigation ownership: [DevPulseNavApp](../../../composeApp/src/commonMain/kotlin/dev/shounakmulay/devpulse/composables/DevPulseNavApp.kt) defines tabRoutes, [DevPulseNavSuiteScaffold](../../../composeApp/src/commonMain/kotlin/dev/shounakmulay/devpulse/composables/DevPulseNavSuiteScaffold.kt) renders tab icons/reselection, and [DevPulseNavDisplay](../../../composeApp/src/commonMain/kotlin/dev/shounakmulay/devpulse/composables/DevPulseNavDisplay.kt) composes feature entry providers. [FeedFeatureEntries](../../../feature/feed/src/commonMain/kotlin/dev/shounakmulay/devpulse/feature/feed/navigation/FeedFeatureEntries.kt) aggregates per-screen entries. Update the relevant owner instead of copying historical App.kt registration snippets. The current empty tab-icon contentDescription is not an accessibility blueprint.
