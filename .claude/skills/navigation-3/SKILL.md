---
name: navigation-3
description: Navigation3 patterns for this project. Use when implementing navigation, adding routes, working with adaptive layouts, or handling back stack behaviour. Covers the project's Screen hierarchy, Navigator API, and ExpandableListDetailScene.
user-invocable: false
---

# Navigation3 in DevPulse

This project uses **androidx.navigation3** with a type-safe sealed interface approach. All navigation knowledge below is specific to how it is wired here.

## Screen hierarchy (`core/navigation/.../Screen.kt`)

```
Screen (sealed interface, implements NavKey)
├── DeveloperTools (data object)
│   └── DesignSystemBoard (data object)
├── Tabs (data object) — hosts NavigationSuiteScaffold
│   ├── Home (data object)
│   ├── Feed (data object)
│   │   └── FeedDetail(id: Int) (data class — carries param)
│   └── Time (data object)
├── Monitors (data object)
└── Settings (data object)
```

All Screen types are `@Serializable` and `@Immutable`. Use `data object` for parameterless screens, `data class` for screens that carry typed arguments. Never use string routes.

## Navigator API

`Navigator` wraps `NavigationState` and exposes:

| Method | Effect |
|---|---|
| `navigate(screen, onRootStack)` | Push screen. `onRootStack=true` pushes to root stack; `false` pushes to the active tab's back stack. |
| `replace(screen, onRootStack)` | Replace the top of the target stack. |
| `replaceOfSameType(screen, onRootStack, addIfNotPresent)` | Replace the top entry if it is the same `Screen` subtype, otherwise push. |
| `navigateBack()` | Pop from the active stack. |

Pass `navigator` down to screen composables that need to trigger navigation — do not access `NavigationState` directly from screens.

## Back stack architecture

- **Root stack** (`navigationState.rootStack`) — top-level destinations (`Tabs`, `DeveloperTools`, etc.). `Screen.Tabs` acts as a single entry that hosts its own per-tab back stacks.
- **Tab back stacks** (`navigationState.tabsBackStacks`) — one `NavBackStack` per tab route. Each tab remembers its own history independently.
- `navigationState.selectedTab` — the currently active tab.

## Registering a screen entry

Screen entries live in `feature/<name>/src/commonMain/.../screens/<screen>/navigation/<Name>ScreenEntry.kt`:

```kotlin
internal fun EntryProviderScope<Screen>.<name>Screen(navigator: Navigator) {
    entry<Screen.Tabs.Home> {
        HomeScreen(viewModel = koinViewModel())
    }
}
```

Feature-level entry points aggregate per-screen functions:

```kotlin
fun EntryProviderScope<Screen>.homeFeatureEntries(navigator: Navigator) {
    homeScreen(navigator)
}
```

All feature entry points are called inside the `entryProvider` lambda in `App.kt`'s `NavDisplay()`.

## Adaptive list-detail layout (Feed pattern)

Use `ExpandableListDetailSceneStrategy` when a feature has a list pane + detail pane:

```kotlin
// Mark the list pane entry
entry<Screen.Tabs.Feed>(
    metadata = ExpandableListDetailSceneStrategy.listPane()
) { FeedScreen(...) }

// Mark the detail pane entry
entry<Screen.Tabs.Feed.FeedDetail>(
    metadata = ExpandableListDetailSceneStrategy.detailPane()
) { FeedDetailScreen(...) }
```

On wide screens (≥ breakpoint, ~600 dp) both panes appear side-by-side. On narrow screens only the active pane is shown. `NavDisplay` in `core/navigation` already composes `ExpandableListDetailSceneStrategy` + `SinglePaneSceneStrategy` — do not configure this manually at the feature level.

## Adding a new tab

1. Add `data object <Name> : Screen` inside `sealed interface Tabs` in `Screen.kt`.
2. Add the screen entry in its feature module.
3. In `App.kt`, add `Screen.Tabs.<Name>` to the `tabRoutes` set and add its icon to the `when` block inside `NavigationSuiteScaffold`.
4. Call `<name>FeatureEntries(navigator)` inside `entryProvider`.

## Common mistakes

- Do **not** push `Screen.Tabs` itself onto the back stack — it is always the root entry. Push individual tab children.
- Do **not** hardcode string routes anywhere; the type-safe `Screen` sealed interface is the only source of truth.
- Deep links and process-death restoration work because all `Screen` types are `@Serializable`. Never add non-serializable fields.
- `Navigator.navigate(screen, onRootStack = false)` pushes within the current tab. Use `onRootStack = true` to show a full-screen destination over the tab bar.

## Further reading

See [Google's official Navigation3 skills](https://github.com/android/skills/tree/main/navigation/navigation-3) for deeper recipes: dialog destinations, result passing, animation customisation, and Hilt/Koin ViewModel integration.
