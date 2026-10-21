# CLAUDE.md

Guidance for Claude Code when working in this repository.

## Project Overview

DevPulse — a Kotlin Multiplatform (KMP) app with Compose Multiplatform UI targeting Android, iOS, and Desktop. Structured as a multi-module Gradle project using convention plugins in `buildSrc`.

## Architecture

Clean Architecture with MVVM/MVI presentation layer. Domain and data layers are planned but not yet implemented — all features are currently presentation-only.

**Dependency direction**: Presentation → Domain → Data. Never reverse.

**Module structure:**
```
:composeApp              # Desktop entry point (also Android via androidApp)
:androidApp              # Android-specific entry point
:iosApp                  # iOS entry point
:feature:home            # Home tab — counter example
:feature:feed            # Feed tab — list/detail adaptive layout
:feature:devtools        # Developer tools & design system showcase
:core:designsystem       # Material3 design tokens, DP-prefixed components, AppTheme
:core:navigation         # Navigation3 integration, Screen hierarchy, Navigator
:core:ui                 # StateEffectViewModel base class, ScreenState, Effect interfaces
```

Use `Modules.kt` constants for all module references — never raw strings.

## Key Patterns

### ViewModel (State / Effect)

Base class is `StateEffectViewModel<STATE : ScreenState>` in `:core:ui`.

Every screen has:
- `MyScreenState` — `@Immutable @Serializable data class` implementing `ScreenState`, everything the UI needs to render
- `MyScreenEffect` — `sealed interface` extending `Effect`, one-time side effects (navigation, toasts)

`state` exposed as `StateFlow`, updated via `setState { copy(...) }`.
`effect` backed by a `Channel`, exposed as `SharedFlow`, emitted via `setEffect(...)`.
Collect state in composables via `val state by viewModel.collectAsState()` (lifecycle-aware).
No business logic in the ViewModel — delegate to use cases when the domain layer exists.

### Screen Composables

Each screen is a single `@Composable` function that:
- Receives the ViewModel via `koinViewModel()`
- Collects state and effects directly
- Uses only `core:designsystem` DP-prefixed components

No Connector/Content split is currently in use.

### Navigation (Navigation3)

Navigation uses **androidx.navigation3** with a type-safe sealed interface hierarchy.

**Screen definitions** (`core/navigation/.../Screen.kt`):
```kotlin
sealed interface Screen : NavKey {
    @Serializable data object DeveloperTools : Screen
    @Serializable data object DesignSystemBoard : Screen

    sealed interface Tabs : Screen {
        @Serializable data object Home : Tabs
        @Serializable data object Feed : Tabs
        @Serializable data class FeedDetail(val id: Int) : Tabs
        @Serializable data object Time : Tabs
    }

    @Serializable data object Monitors : Screen
    @Serializable data object Settings : Screen
}
```

All Screen types are `@Serializable` for state restoration. Screens with parameters are data classes. Never use string-based routes.

**Key navigation classes:**
- `NavigationState` — manages `rootStack` and `tabsBackStacks` as `NavBackStack<Screen>`
- `Navigator` — provides `navigate()`, `replace()`, `replaceOfSameType()`, `navigateBack()`
- `NavDisplay()` — composable combining `ExpandableListDetailSceneStrategy` + `SinglePaneSceneStrategy`

### Adaptive List-Detail Layout

The Feed module uses `ExpandableListDetailScene` for tablet/large-screen adaptive layouts. Panes are classified via entry metadata:
- List pane: `ExpandableListDetailSceneStrategy.listPane()`
- Detail pane: `ExpandableListDetailSceneStrategy.detailPane()`

On wide screens (≥ breakpoint, typically 600.dp) both panes appear side by side; on narrow screens only one pane is shown at a time. Follow this pattern for any new list-detail feature.

### Dependency Injection

**Koin** with annotation-based zero-boilerplate DI across all platforms.

- `@Module @ComponentScan("dev.shounakmulay.devpulse.feature.<name>")` on a class in each feature's `di/` package
- `@KoinViewModel` on every ViewModel class
- ViewModels obtained via `koinViewModel()` from koin-compose-viewmodel

Feature module DI structure:
```
feature:xxx/
└── di/
    └── XxxModule.kt   # @Module @ComponentScan("dev.shounakmulay.devpulse.feature.xxx")
```

Root DI wiring is in `composeApp/.../di/DevPulseKoinApplication.kt` via `@KoinApplication`.

### Design System

All UI components come from `:core:designsystem` and are prefixed with `DP`:

- **Text**: `DPTextView` (pick a `DPTextViewVariant` for Display/Heading/Title/Body/Label × size/emphasis; pass `fontFamily = monoFontFamily()` for mono)
- **Buttons**: `DPButton`, `DPElevatedButton`, `DPOutlinedButton`, `DPTextButton`
- **Lists**: `DPClickableRow`, `DPLists`
- **Input**: `DPTextFields`
- **Navigation**: DP navigation components
- **Feedback**: `DPFABs`, `DPBadge`, `DPDividers`

Design tokens are accessed via `DPTheme.spacing` and `DPTheme.iconSize` (injected as `LocalDPSpacing` and `LocalDPIconSize` by `AppTheme`).

**Never use raw Material3 primitives directly** — stop and ask if a needed component is missing from the design system.

The `:feature:devtools` module contains a comprehensive component gallery (`DesignSystemBoard`) used for development only.

### Build System (Convention Plugins)

All complexity is hidden in `buildSrc`. Apply the right plugin per module:

| Plugin alias | Use for |
|---|---|
| `devpulse.kmp.library` | Non-UI core modules |
| `devpulse.kmp.library.compose` | Feature modules and UI core modules |
| `devpulse.kmp.android.application` | App entry-point modules |

Each plugin automatically configures Kotlin Multiplatform targets (Android, iosArm64, iosSimulatorArm64, JVM), Koin compiler, and Compose/Material3 stacks where applicable.

### Platform-Specific Code

```
src/
├── commonMain/    # All shared code; default for new code
├── androidMain/   # Android-specific (e.g. ColorSchemeProvider, Platform)
├── iosMain/       # iOS-specific
├── jvmMain/       # Desktop-specific
├── commonTest/    # Shared unit tests
├── androidHostTest/    # Android JVM unit tests
└── androidDeviceTest/  # Android instrumented tests (on-device)
```

Platform variation uses `expect`/`actual`. The most common uses are `Platform.kt` (platform detection) and `ColorSchemeProvider.kt` (dynamic color on Android, static on iOS/Desktop).

### Libraries in Use

- **kotlinx.serialization** — JSON and Screen state serialization; never Gson/Moshi/Jackson in `commonMain`
- **Koin** — DI (annotations + compiler plugin)
- **androidx.navigation3** — Type-safe navigation
- **Compose Multiplatform + Material3** — UI (Adaptive, NavigationSuite, WindowSizeClass, Expressive)
- **Lifecycle + ViewModel** — `collectAsStateWithLifecycle()`, `viewModelCompose`

Ktor and Room are planned but not yet present in the codebase.

## Coding Standards

- **No comments** — code is the documentation. Exceptions: non-obvious math, unintuitive platform workarounds, intentional deviations that would look like bugs
- **No `Any` type** — use proper types or a bounded generic
- **`val` over `var`** — `data class` + `copy()` for all state updates
- **`Result<T>`** — never throw exceptions from use cases or repositories
- **Named arguments** for functions with 3+ parameters or non-obvious booleans
- **`when` over if-else chains** for sealed types or 3+ conditions
- **`StateFlow`** for UI state, **`SharedFlow`** for one-time effects
- **`@Serializable`** on all `Screen` types and `ScreenState` data classes
- **`@KoinViewModel`** on all ViewModel classes
- **Design system first** — always use `core:designsystem` DP-prefixed components; stop and ask if something is missing before using raw Material3 primitives
- **No `.md` files** created to explain code changes

## Testing

- **Frameworks**: `kotlin.test`, MockK, Turbine, `kotlinx-coroutines-test`
- **Naming**: Given-When-Then — `` `Given X When Y Then Z`() ``
- **Turbine is the only way to assert on Flows** — never `first()`, `toList()`, or manual `collect`
- **Mock only external boundaries** — real instances for mappers, data classes, pure utilities
- Test files live in `commonTest` or `androidHostTest`, mirroring the production package

## Exploration Rules

- Read only files directly relevant to the task
- Never explore `build/`, `.gradle/`, or generated output directories
- Read a module's entry point first before going deeper

## Task Workflow

1. **Understand** — read only the directly affected files
2. **Plan** — state what you'll change and why; wait for confirmation on non-trivial tasks
3. **Execute** — make changes, then run `./gradlew ktlintCheck test`
4. **Stop** — no explanatory prose, no extra files

## When Uncertain

- Do not speculatively read more files hoping to find the answer
- State what you need and ask directly
