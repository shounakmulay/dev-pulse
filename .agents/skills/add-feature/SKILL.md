---
name: add-feature
description: Scaffold a complete new KMP feature module in the DevPulse project. Use when adding a new feature tab, top-level screen, or standalone section of the app. Creates all files and wires all registrations.
argument-hint: "[feature-name] [screen-type: tabs|root]"
disable-model-invocation: true
allowed-tools: Read Glob Grep Write Edit Bash(mkdir *)
---

Scaffold a new KMP feature module named **$ARGUMENTS**.

Parse the argument as `<feature-name> [screen-type]`. Default screen-type is `tabs` (appears as a tab inside `Screen.Tabs`). Use `root` for a top-level root-stack screen.

## Naming conventions

From the feature name `$0` (e.g. `notifications`):
- Package segment: `$0` (lowercase, no hyphens)
- Class prefix: PascalCase (e.g. `Notifications`)
- Module path: `:feature:$0`
- iOS framework base name: `core:$0Kit`
- Gradle namespace: `dev.shounakmulay.devpulse.feature.$0`

## Step 1 — Add Screen type to `Screen.kt`

File: `core/navigation/src/commonMain/kotlin/dev/shounakmulay/core/navigation/Screen.kt`

For **tabs** screen, add inside `sealed interface Tabs`:
```kotlin
@Serializable
data object <Name> : Screen
```

For **root** screen, add as a top-level entry in `Screen`:
```kotlin
@Serializable
data object <Name> : Screen
```

## Step 2 — Add module constant to `Modules.kt`

File: `buildSrc/src/main/kotlin/dev/shounakmulay/devpulse/buildsrc/constants/Modules.kt`

Add inside `object Feature`:
```kotlin
const val <NAME_UPPER> = ":feature:$0"
```

## Step 3 — Register in `settings.gradle.kts`

File: `settings.gradle.kts` (root)

Add:
```
include(":feature:$0")
```

## Step 4 — Create module directory and `build.gradle.kts`

Create `feature/$0/build.gradle.kts`:
```kotlin
import dev.shounakmulay.devpulse.buildsrc.constants.Modules
import dev.shounakmulay.devpulse.buildsrc.extensions.iosFrameworks

plugins {
    alias(libs.plugins.devpulse.kmp.library.compose)
}

kotlin {
    android {
        namespace = "dev.shounakmulay.devpulse.feature.$0"

        withHostTestBuilder {}

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }
    iosFrameworks(baseName = "core:$0Kit")

    sourceSets {
        commonMain.dependencies {
            implementation(project(Modules.Core.NAVIGATION))
            implementation(project(Modules.Core.UI))
            implementation(libs.compose.components.resources)
        }
    }
}
```

## Step 5 — Create Koin DI module

Create `feature/$0/src/commonMain/kotlin/dev/shounakmulay/feature/$0/di/<Name>Module.kt`:
```kotlin
package dev.shounakmulay.devpulse.feature.$0.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module
@ComponentScan("dev.shounakmulay.devpulse.feature.$0")
class <Name>Module
```

## Step 6 — Create ScreenState

Create `feature/$0/src/commonMain/kotlin/dev/shounakmulay/feature/$0/screens/$0/ui/<Name>ScreenState.kt`:
```kotlin
package dev.shounakmulay.devpulse.feature.$0.screens.$0.ui

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class <Name>ScreenState(
    val isLoading: Boolean = false,
) : ScreenState
```

## Step 7 — Create ViewModel

Create `feature/$0/src/commonMain/kotlin/dev/shounakmulay/feature/$0/screens/$0/ui/<Name>ViewModel.kt`:
```kotlin
package dev.shounakmulay.devpulse.feature.$0.screens.$0.ui

import dev.shounakmulay.devpulse.core.ui.viewmodel.StateEffectViewModel
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class <Name>ViewModel : StateEffectViewModel<<Name>ScreenState>() {
    override fun createInitialState() = <Name>ScreenState()
}
```

## Step 8 — Create Screen composable

Create `feature/$0/src/commonMain/kotlin/dev/shounakmulay/feature/$0/screens/$0/ui/<Name>Screen.kt`:
```kotlin
package dev.shounakmulay.devpulse.feature.$0.screens.$0.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant

@Composable
fun <Name>Screen(viewModel: <Name>ViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.collectAsState()
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        DPTextView(text = "<Name>", variant = DPTextViewVariant.HeadingLarge)
    }
}
```

## Step 9 — Create screen navigation entry

Create `feature/$0/src/commonMain/kotlin/dev/shounakmulay/feature/$0/screens/$0/navigation/<Name>ScreenEntry.kt`:
```kotlin
package dev.shounakmulay.devpulse.feature.$0.screens.$0.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.feature.$0.screens.$0.ui.<Name>Screen
import org.koin.compose.viewmodel.koinViewModel

internal fun EntryProviderScope<Screen>.<$0>Screen(navigator: Navigator) {
    entry<Screen.Tabs.<Name>> {   // adjust to Screen.<Name> if root screen
        <Name>Screen(viewModel = koinViewModel())
    }
}
```

## Step 10 — Create feature entries

Create `feature/$0/src/commonMain/kotlin/dev/shounakmulay/feature/$0/navigation/<Name>FeatureEntries.kt`:
```kotlin
package dev.shounakmulay.devpulse.feature.$0.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.feature.$0.screens.$0.navigation.<$0>Screen

fun EntryProviderScope<Screen>.<$0>FeatureEntries(navigator: Navigator) {
    <$0>Screen(navigator)
}
```

## Step 11 — Register DI module in `DevPulseKoinApplication.kt`

File: `composeApp/src/commonMain/kotlin/dev/shounakmulay/devpulse/di/DevPulseKoinApplication.kt`

Add `<Name>Module::class` to the `@KoinApplication` modules list and add the import.

## Step 12 — Register navigation entries in `App.kt`

File: `composeApp/src/commonMain/kotlin/dev/shounakmulay/devpulse/App.kt`

Inside the `entryProvider` lambda in the `NavDisplay()` call, add:
```kotlin
<$0>FeatureEntries(navigator)
```

For a **tabs** screen, also add `Screen.Tabs.<Name>` to the `tabRoutes` set and add a branch in the icon `when` expression.

## Step 13 — Verify

Run: `./gradlew :feature:$0:assemble`
