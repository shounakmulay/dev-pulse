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

# [DevPulse] recent context, 2026-08-07 2:46pm GMT+5:30

Legend: 🎯session 🔴bugfix 🟣feature 🔄refactor ✅change 🔵discovery ⚖️decision 🚨security_alert 🔐security_note
Format: ID TIME TYPE TITLE
Fetch details: get_observations([IDs]) | Search: mem-search skill

Stats: 50 obs (11,260t read) | 713,135t work | 98% savings

### Jun 10, 2026
S43 Refine feed entity indexes and filters based on trade-offs. (Jun 10 at 7:16 PM)
S44 Set Default Model (Jun 10 at 7:19 PM)
### Jun 11, 2026
S47 Clarification on "auth code" request (Jun 11 at 5:34 PM)
### Jul 15, 2026
S48 Explanation of device-code login flow (Jul 15 at 4:36 PM)
S49 Create and push feature branch, then open a pull request (Jul 15 at 4:38 PM)
### Jul 23, 2026
S50 Scaffold :bridge:markdownconverter module and investigate project structure. (Jul 23 at 2:55 PM)
### Jul 31, 2026
1880 9:56a 🔵 Module Inclusion in settings.gradle.kts
1881 " 🔵 DevPulse BuildSrc Constants - Modules Object
1882 " 🔵 Core Module build.gradle.kts Files
S51 Create subfolders in modules based on package names (Jul 31 at 9:57 AM)
1885 9:58a ✅ Added :bridge:markdownconverter module to project
1886 " ✅ Successfully compiled JVM target for :bridge:markdownconverter
1888 " 🔵 Gradle Task Graph Calculation for :bridge:markdownconverter
1890 " 🔵 BuildSrc Task Execution Status
1892 " 🔵 Empty Output from Gradle Execution
1896 " 🔵 Empty Output from Gradle Execution
1904 " ✅ Successful Gradle Build with Module Compilation
1906 9:59a ✅ Modified files related to new module integration
1909 10:00a 🔵 Directory structure of core/network module
1934 10:12a 🟣 Subfolder Creation Based on Package Name
1935 " ✅ Organized Module Structure with Package-Named Subfolders
S53 Investigate and resolve the missing native libraries for the html-to-markdown-android dependency. (Jul 31 at 10:12 AM)
1936 10:58a 🔵 32-bit ABI Filters Identified in Android Build Files
1937 " 🔵 Presence of 32-bit Native Libraries Detected
1938 " 🔵 Transitive Dependency Forces 32-bit Architectures
1939 " 🔵 CI/CD Pipeline Configured for Universal APKs
1940 " ✅ Removal of 32-bit ABI Filters
1941 " ✅ Exclusion of 32-bit Native Libraries
1942 " ✅ Dependency Update for 64-bit Compatibility
1943 " ✅ CI/CD Pipeline Modified for 64-bit Only Builds
1944 " 🔵 Build Configuration Files Identified
1945 10:59a 🔵 Gradle Wrapper Lock File Error
1946 11:00a 🔵 Gradle Dependency Analysis for 32-bit Architectures
1947 " 🔵 Analysis of Android Runtime Dependencies
1956 4:41p 🔴 HTML to Markdown Android dependency artifact missing native code
1957 " 🔵 HTML to Markdown Android dependency identified in libs.versions.toml
1958 " 🔵 HTML to Markdown Android dependency configuration confirmed
1959 " 🔵 HTML to Markdown Android API details confirmed
1961 4:42p 🔵 Gradle dependency tree for HTML to Markdown Android confirmed
1962 " 🔴 HTML to Markdown Android dependency resolution issue
1964 4:43p 🔵 HTML to Markdown Android compile classpath analysis
1966 " 🔴 HTML to Markdown Android AAR missing native libraries
S54 Verify Android build fix for html-to-markdown-android (Jul 31 at 4:43 PM)
### Aug 1, 2026
1988 4:47p 🔴 Android build issue resolved in version 3.10.1
1989 " 🔵 Verified Android AAR contents for version 3.10.1
### Aug 3, 2026
1990 1:02p 🔵 Xcode linker error for header-only C target
1991 " ⚖️ Introduce modulemap for header-only C target
1992 " 🔴 Fix Xcode linking error for header-only C target
1993 " ⚖️ Ranked solutions for Xcode header-only C target issue
S55 Diagnose and fix Xcode linking error for header-only C target in Swift Package Manager. (Aug 3 at 1:03 PM)
### Aug 4, 2026
1994 4:20p 🟣 Add sqlite-vector JVM dependency
1995 " 🔵 Investigate sqlite-vector dependency in project
1996 " ✅ Add JVM dependency for sqlite-vector
1998 " ✅ Compile Kotlin JVM for sqlitevec module
2000 " 🔵 Gradle task graph calculation for sqlitevec
2004 " 🔵 BuildSrc compilation tasks status
2008 " 🔵 Gradle build execution completion
2016 4:21p 🔵 Gradle tasks execution and configuration cache
2021 4:23p 🔵 SQLite-Vector extension for JVM and Android
### Aug 7, 2026
2028 2:46p 🟣 Automated PR creation and commit detailing

Access 713k tokens of past work via get_observations([IDs]) or mem-search skill.
</claude-mem-context>

<!-- SPECKIT START -->
For additional context about technologies to be used, project structure,
shell commands, and other important information, read specs/001-feed-query-engine/plan.md
<!-- SPECKIT END -->
