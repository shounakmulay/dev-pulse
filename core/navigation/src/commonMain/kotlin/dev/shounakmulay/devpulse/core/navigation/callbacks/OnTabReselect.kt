package dev.shounakmulay.devpulse.core.navigation.callbacks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun OnTabReselect(navigator: Navigator, tab: Screen, action: suspend () -> Unit) {
    LaunchedEffect(Unit) {
        navigator.reselectEvents(tab).collectLatest {
            action()
        }
    }
}