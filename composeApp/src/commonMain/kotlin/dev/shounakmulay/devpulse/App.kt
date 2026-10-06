package dev.shounakmulay.devpulse

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import dev.shounakmulay.devpulse.composables.DevPulseContextProviders
import dev.shounakmulay.devpulse.composables.DevPulseNavApp
import dev.shounakmulay.devpulse.composables.DevPulseThemedApp


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun App() {
    DevPulseThemedApp {
        DevPulseContextProviders {
            DevPulseNavApp()
        }
    }
}
