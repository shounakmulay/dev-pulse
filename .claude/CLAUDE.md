# CLAUDE.md

Guidance for Claude Code when working in this repository.

## Project Overview

DevPulse — a Kotlin Multiplatform (KMP) app with Compose Multiplatform UI targeting Android, iOS, and Desktop. Structured as a multi-module Gradle project using convention plugins in `buildSrc`.

## Architecture

Clean Architecture with MVVM/MVI presentation layer.

**Dependency direction**: Presentation → Domain → Data. Never reverse.

**Module structure:**
```
:composeApp           # Android + Desktop entry point
:iosApp               # iOS entry point
:feature:<name>       # Feature module (presentation + domain + data)
:core:designsystem    # Compose Multiplatform design tokens & components
:core:ui              # Shared reusable UI components
:core:domain          # Shared domain models, base use case, Result
:core:network         # Ktor client, API definitions
:core:database        # Room entities, DAOs, and database definitions
:core:common          # Extensions, utilities, base classes
:core:logging         # Logger abstraction + platform implementations
:core:test            # Test utilities, fakes, test dispatchers
```

Use `Modules.kt` constants for all module references — never raw strings.

## Key Patterns

### ViewModel (State / Event / Effect)

Every screen has three companion files:
- `MyScreenState` — `@Immutable data class`, everything the UI needs to render
- `MyScreenEvent` — `sealed interface`, user actions dispatched from the UI
- `MyScreenEffect` — `sealed interface`, one-time side effects (navigation, toasts)

`state` exposed as `StateFlow`, updated via `setState { copy(...) }`.
`effect` exposed as `SharedFlow`, emitted via `setEffect(...)`.
No business logic in the ViewModel — delegate to use cases.

### Connector / Content

Every screen composable is split into:
- **Connector** — obtains ViewModel via `koinViewModel()`, collects state/effects, not previewable
- **Content** — pure stateless composable, data-in only, always previewable

### Use Cases

Single `invoke()`, always returns `Result<T>`, switches dispatchers internally with `withContext(Dispatchers.IO)`.

### Dependency Injection

**Koin** across all platforms. One module per layer per feature: `featureNameDomainModule`, `featureNameDataModule`, `featureNamePresentationModule`.

### Platform Libraries

- **Ktor** — HTTP client (OkHttp on Android, Darwin on iOS)
- **Room KMP** (≥ 2.7) — local persistence; entities/DAOs in `commonMain`
- **kotlinx.serialization** — JSON; never Gson/Moshi/Jackson in `commonMain`

## Coding Standards

- **No comments** — code is the documentation. Exceptions: non-obvious math, unintuitive platform workarounds, intentional deviations that would look like bugs
- **No `Any` type** — use proper types or a bounded generic
- **`val` over `var`** — `data class` + `copy()` for all state updates
- **`Result<T>`** — never throw exceptions from use cases or repositories
- **Named arguments** for functions with 3+ parameters or non-obvious booleans
- **`when` over if-else chains** for sealed types or 3+ conditions
- **`StateFlow`** for UI state, **`SharedFlow`** for one-time effects
- **Design system first** — always use `core:designsystem` components; stop and ask if something is missing before using raw Material3 primitives
- **No `.md` files** created to explain code changes

## Testing

- **Frameworks**: `kotlin.test`, MockK, Turbine, `kotlinx-coroutines-test`
- **Naming**: Given-When-Then — `` `Given X When Y Then Z`() ``
- **Turbine is the only way to assert on Flows** — never `first()`, `toList()`, or manual `collect`
- **Mock only external boundaries** — real instances for mappers, data classes, pure utilities
- Test files live in `commonTest` or `androidUnitTest`, mirroring the production package

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
