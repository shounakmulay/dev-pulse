package dev.shounakmulay.devpulse.core.ui.preferences

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.shounakmulay.devpulse.core.preferences.DevPulsePreferenceKeys
import dev.shounakmulay.devpulse.core.preferences.DevPulsePreferences
import kotlinx.coroutines.flow.filterNotNull
import org.koin.compose.koinInject

@Composable
fun isAppInBlackMode(): Boolean {
    val preferences: DevPulsePreferences = koinInject()
    var isAppInBlackMode by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(preferences) {
        preferences.observe(DevPulsePreferenceKeys.isAppInBlackMode)
            .filterNotNull()
            .collect {
                isAppInBlackMode = it
            }
    }

    return isAppInBlackMode
}