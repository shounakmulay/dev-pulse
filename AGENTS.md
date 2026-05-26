# AGENTS.md

Guidance for Codex when working in this repository.

## Project Overview

DevPulse — a Kotlin Multiplatform (KMP) app with Compose Multiplatform UI targeting Android, iOS, and Desktop. Structured as a multi-module Gradle project using convention plugins in `buildSrc`.

## Architecture

Clean Architecture with an MVI presentation layer. Domain and data modules exist for settings, and feed has a scaffolded data module for network-backed implementation work.

**Dependency direction**: Presentation → Domain → Data. Never reverse.

**Module structure:**
```
:composeApp              # Desktop entry point (also Android via androidApp)
:androidApp              # Android-specific entry point
:iosApp                  # iOS Xcode project, not included as a Gradle module
:feature:home            # Home tab — counter example
:feature:feed            # Feed tab — list/detail adaptive layout
:feature:devtools        # Developer tools & design system showcase
:feature:settings        # Settings tab and developer links
:core:designsystem       # Material3 design tokens, DP-prefixed components, AppTheme
:core:navigation         # Navigation3 integration, Screen hierarchy, Navigator
:core:ui                 # MviViewModel base class, ScreenState, Effect, EventHandler, text helpers
:core:resources          # Compose resources and generated resource access
:core:network            # Ktor client and platform engines
:core:preferences        # Multiplatform DataStore preferences
:core:common             # Shared utilities and extensions
:core:domain:models      # Shared domain models
:core:domain:settings    # Settings use cases
:core:data:settings      # Settings repository implementation
:core:data:feed          # Feed data module scaffold, depends on network
```

Use `Modules.kt` constants for all module references — never raw strings.

## Key Patterns

### ViewModel (State / Effect)

Base class is `MviViewModel<STATE : ScreenState, EFFECT : Effect>` in `:core:ui`.

Every screen has:
- `MyScreenState` — `@Immutable @Serializable data class` implementing `ScreenState`, everything the UI needs to render
- `MyScreenEffect` — `sealed interface` extending `Effect`, one-time side effects (navigation, toasts)
- `MyScreenEvent` — sealed event type handled by the ViewModel through `EventHandler<MyScreenEvent>` when the screen has user events

`state` is exposed as `StateFlow` from Orbit's container and updated via `setState { copy(...) }`. One-time effects are posted with `postEffect(...)`.
Collect state in composables via Orbit's `val state by viewModel.collectAsState()`, or use the shared `core:ui` screen helpers when appropriate.
No business logic in the ViewModel — delegate to use cases when a domain boundary exists.

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
    @Serializable data object DeveloperTools {
        @Serializable data object DesignSystemBoard : Screen
    }

    @Serializable data object Tabs : Screen {
        @Serializable data object Home : Screen
        @Serializable data object Feed : Screen {
            @Serializable data class FeedDetail(val id: Int) : Screen
        }
        @Serializable data object Time : Screen
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
- Core/data/domain modules use the same annotation pattern with their package root, e.g. `dev.shounakmulay.devpulse.core.data.settings`
- `@KoinViewModel` on every ViewModel class
- ViewModels obtained via `koinViewModel()` from koin-compose-viewmodel

Feature module DI structure:
```
feature:xxx/
└── di/
    └── XxxModule.kt   # @Module @ComponentScan("dev.shounakmulay.devpulse.feature.xxx")
```

Root DI wiring is in `composeApp/.../di/DevPulseKoinApplication.kt` via `@KoinApplication`. Keep module composition at the app root when a module needs to participate in the app graph.

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
- **Lifecycle + ViewModel** — lifecycle ViewModel + Compose integration
- **Orbit MVI** — container-backed ViewModel state and side effects
- **Ktor** — network client in `:core:network`
- **DataStore Preferences** — persistence in `:core:preferences`

Room is planned but not yet present in the codebase.

## Coding Standards

- **No comments** — code is the documentation. Exceptions: non-obvious math, unintuitive platform workarounds, intentional deviations that would look like bugs
- **No `Any` type** — use proper types or a bounded generic
- **`val` over `var`** — `data class` + `copy()` for all state updates
- **`Result<T>`** — never throw exceptions from use cases or repositories
- **Named arguments** for functions with 3+ parameters or non-obvious booleans
- **`when` over if-else chains** for sealed types or 3+ conditions
- **`StateFlow`** for UI state, Orbit side effects for one-time effects
- **`@Serializable`** on all `Screen` types and `ScreenState` data classes
- **`@KoinViewModel`** on all ViewModel classes
- **Design system first** — always use `core:designsystem` DP-prefixed components; stop and ask if something is missing before using raw Material3 primitives
- **No `.md` files** created to explain code changes

## Testing

- **Frameworks currently wired**: `kotlin.test`, JUnit, AndroidX test, Orbit test
- **Naming**: Given-When-Then — `` `Given X When Y Then Z`() ``
- **Mock only external boundaries** — real instances for mappers, data classes, pure utilities
- Test files live in `commonTest` or `androidHostTest`, mirroring the production package

## Exploration Rules

- Read only files directly relevant to the task
- Never explore `build/`, `.gradle/`, or generated output directories
- Read a module's entry point first before going deeper

## Task Workflow

1. **Understand** — read only the directly affected files
2. **Plan** — state what you'll change and why; wait for confirmation on non-trivial tasks
3. **Execute** — make changes, then run a targeted Gradle verification for the affected module, usually `./gradlew :<module>:compileKotlinIosSimulatorArm64` for shared KMP changes or the relevant `:<module>:jvmTest` when tests exist
4. **Stop** — no explanatory prose, no extra files

`ktlintCheck` and `ktlintFormat` are not registered tasks in this repo.

For commit workflows, extract the ticket prefix from the current branch name using the first `ABC-123`-style match. If no ticket-style prefix exists, proceed without one when the user approves.

## When Uncertain

- Do not speculatively read more files hoping to find the answer
- State what you need and ask directly


<claude-mem-context>
# Memory Context

# [DevPulse] recent context, 2026-05-26 10:31am GMT+5:30

Legend: 🎯session 🔴bugfix 🟣feature 🔄refactor ✅change 🔵discovery ⚖️decision 🚨security_alert 🔐security_note
Format: ID TIME TYPE TITLE
Fetch details: get_observations([IDs]) | Search: mem-search skill

Stats: 25 obs (6,006t read) | 487,302t work | 99% savings

### May 19, 2026
721 9:23a ✅ Initial User Request
### May 25, 2026
722 8:01p 🟣 Implement detailed commit generation
723 " 🔵 Current Git branch identified
724 " 🔵 Modified and untracked files identified
726 " 🔄 Refactored navigation scene strategy logic
729 8:02p 🔵 ObserveFeedQueueForUrlsUseCase code snippet retrieved
731 " 🔵 FeedsHeaderDivider composable code snippet retrieved
735 " ✅ Staged specific data and domain files for commit
741 " 🔄 Refined SQL query in FeedQueueDao
747 " ✅ Staged FeedQueueDao and RssFeedQueueRepository for commit
756 8:03p ✅ Staged changes in design system files
766 " ✅ Staged Add Feed screen files for commit
768 " ✅ No trailing whitespace issues found in staged changes
786 8:04p ✅ AGENTS.md file modified and unstaged
800 " ✅ Repository is clean
### May 26, 2026
818 7:38a 🟣 Add "Retry Failed" button functionality
819 " 🟣 Add "Edit" button for failed feed items
820 7:39a 🔵 AddFeedScreen UI structure
821 " 🔵 AddFeedViewModel event handling
822 " 🔵 UIFeedQueueData structure
824 " 🔵 ImportFeedUseCase functionality
826 " 🔵 RssFeedQueueRepository interface and implementation
831 " 🔵 Database models and DAO for Feed Queue
837 10:01a 🔄 Extract Add Feed UI Strings to TextResource
838 10:04a 🔵 Add Feed Screen Text Resource Usage

Access 487k tokens of past work via get_observations([IDs]) or mem-search skill.
</claude-mem-context>