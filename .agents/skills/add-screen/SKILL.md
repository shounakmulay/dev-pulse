---
name: add-screen
description: Add a new screen to an existing DevPulse feature module. Creates ScreenState, ViewModel, Screen composable, and navigation entry, then wires it into the feature's entry point.
argument-hint: "[feature-name] [screen-name]"
disable-model-invocation: true
allowed-tools: Read Glob Grep Write Edit
---

Add a new screen to an existing feature module.

Feature: **$0**
Screen: **$1**

From `$1` derive the class prefix (PascalCase) — e.g. `feed-detail` → `FeedDetail`.

## Step 1 — Add Screen type to `Screen.kt`

File: `core/navigation/src/commonMain/kotlin/dev/shounakmulay/core/navigation/Screen.kt`

Add the new `Screen` type in the appropriate location (inside a parent `object` if it is a nested route, or at the top level):
```kotlin
@Serializable
@Immutable
data class <Name>(/* typed params if needed */) : Screen
```

Use `data object` when the screen has no parameters, `data class` when it carries typed navigation arguments.

## Step 2 — Create ScreenState

Create `feature/$0/src/commonMain/kotlin/dev/shounakmulay/feature/$0/screens/$1/ui/<Name>ScreenState.kt`:
```kotlin
package dev.shounakmulay.devpulse.feature.$0.screens.$1.ui

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class <Name>ScreenState(
    val isLoading: Boolean = false,
) : ScreenState
```

## Step 3 — Create ViewModel

Create `feature/$0/src/commonMain/kotlin/dev/shounakmulay/feature/$0/screens/$1/ui/<Name>ViewModel.kt`:
```kotlin
package dev.shounakmulay.devpulse.feature.$0.screens.$1.ui

import dev.shounakmulay.devpulse.core.ui.viewmodel.StateEffectViewModel
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class <Name>ViewModel : StateEffectViewModel<<Name>ScreenState>() {
    override fun createInitialState() = <Name>ScreenState()
}
```

## Step 4 — Create Screen composable

Create `feature/$0/src/commonMain/kotlin/dev/shounakmulay/feature/$0/screens/$1/ui/<Name>Screen.kt`:
```kotlin
package dev.shounakmulay.devpulse.feature.$0.screens.$1.ui

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

## Step 5 — Create navigation entry

Create `feature/$0/src/commonMain/kotlin/dev/shounakmulay/feature/$0/screens/$1/navigation/<Name>ScreenEntry.kt`:
```kotlin
package dev.shounakmulay.devpulse.feature.$0.screens.$1.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.feature.$0.screens.$1.ui.<Name>Screen
import org.koin.compose.viewmodel.koinViewModel

internal fun EntryProviderScope<Screen>.<$1>Screen(navigator: Navigator) {
    entry<<FullScreenType>> {
        <Name>Screen(viewModel = koinViewModel())
    }
}
```

Replace `<FullScreenType>` with the qualified type from `Screen.kt` (e.g. `Screen.Tabs.Feed.FeedDetail`).

If the screen receives navigation parameters from the entry lambda, extract them and pass to the ViewModel or composable as needed.

## Step 6 — Register in feature entries

File: `feature/$0/src/commonMain/kotlin/dev/shounakmulay/feature/$0/navigation/<Feature>FeatureEntries.kt`

Add a call to the new screen entry function inside the `EntryProviderScope` lambda.

## Step 7 — Verify

Run `./gradlew :feature:$0:assemble` and fix any compilation errors.
