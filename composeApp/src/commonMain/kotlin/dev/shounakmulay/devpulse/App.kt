package dev.shounakmulay.devpulse

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.shounakmulay.devpulse.composables.DevPulseThemedApp
import dev.shounakmulay.devpulse.di.koinConfiguration
import org.koin.compose.KoinApplication


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
@Preview
fun App() {
    KoinApplication(configuration = koinConfiguration) {
        DevPulseThemedApp()
    }
}
