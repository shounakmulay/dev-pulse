---
description: Core Kotlin Multiplatform coding standards for DevPulse
---

# DevPulse KMP Coding Standards

## Comments

**The code is the documentation.** Comments must not narrate what the code does.

- **No inline explanations** — if a line needs a comment to be understood, rename or restructure it
- **No section dividers / banners** — `// ──────── SECTION ────────` blocks are noise; file structure and function names provide the navigation
- **No enum variant annotations** — `Healthy, // Operational / success — jade` tells the reader nothing a well-named enum and its usages don't already show
- **No usage guidance in code** — "Never use X here, use Y instead" belongs in the rules, not a source file
- **Exceptions (rare)** — a comment is justified when the code cannot speak for itself:
  - Non-obvious math or algorithm (e.g., `// Haversine formula — result in radians`)
  - Unintuitive platform assumption or workaround (e.g., `// iOS reuses the controller on hot restart; guard against double-init`)
  - Intentional deviation from the normal pattern that would otherwise look like a bug

## Kotlin Style

- **No `Any` type** — use proper types or a bounded generic; avoid `Any` as a catch-all
- **No hardcoded strings in logic** — use constants, sealed classes, or resource wrappers
- **Named arguments** for functions with 3+ parameters or non-obvious boolean parameters
- **Trailing comma** on all multi-line argument lists and when blocks
- **`when` over if-else chains** when branching on a sealed type or 3+ conditions

## File & Package Naming

- **PascalCase** for files containing a single class/interface matching the file name
- **camelCase** for files containing top-level functions or extensions (e.g., `flowExtensions.kt`)
- Package names: all lowercase, no underscores — `dev.shounakmulay.devpulse.feature.auth`
- Platform-specific files: use the platform suffix in the filename — `Platform.android.kt`, `Platform.ios.kt`
- Do **not** add platform suffixes in `commonMain` files

## Immutability

- Prefer `val` over `var` everywhere; only use `var` when mutation is unavoidable
- Use `data class` with `copy()` for state updates — never mutate state in place
- Mark all UI state data classes with `@Immutable`

## Error Handling

- **Never throw exceptions** from use cases or repositories up through the call stack
- Wrap results in a `Result<T>` sealed class (`Success`/`Error`) — no raw exceptions in ViewModels
- Log all errors before wrapping — use the project logger, not `println` or `System.out`

## Coroutines & Flow

- **StateFlow** for UI state — holds current value, replays to new collectors
- **SharedFlow** for one-time effects (navigation, dialogs, toasts) — no persistent state
- Scope all launched coroutines to the appropriate `CoroutineScope` (viewModelScope, lifecycleScope)
- Use `distinctUntilChanged()` when observing flows that may emit identical values repeatedly
- Switch dispatchers inside use cases, not in ViewModels: `withContext(Dispatchers.IO) { ... }`
- Never block the main thread — all I/O must use suspend functions or flows

## Logging

- Every class that logs must define `private const val TAG = "ClassName"` in its companion object
- Use structured logging levels: `logger.info`, `logger.debug`, `logger.warn`, `logger.error`
- Log at entry points of public/important functions with key parameters
- Log both success and error branches of `Result` handling

## Documentation

- **No `.md` files** created to explain code changes or implementations
- Only add KDoc to public API surface in `commonMain` that is non-obvious
