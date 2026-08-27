package dev.shounakmulay.devpulse

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import dev.shounakmulay.devpulse.composables.DevPulseContextProviders
import dev.shounakmulay.devpulse.composables.DevPulseNavApp
import dev.shounakmulay.devpulse.composables.DevPulseThemedApp
import dev.shounakmulay.devpulse.core.sync.BackgroundSyncScheduler
import dev.shounakmulay.devpulse.core.sync.DevPulseWorkRequest
import dev.shounakmulay.devpulse.core.sync.DevPulseWorkerType
import dev.shounakmulay.devpulse.di.koinConfiguration
import dev.shounakmulay.devpulse.logging.DevPulseLogging
import org.koin.compose.KoinApplication
import org.koin.compose.getKoin


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
@Preview
fun App() {
    DevPulseLogging.configure()
    KoinApplication(configuration = koinConfiguration) {
        DevPulseThemedApp {
            DevPulseContextProviders {
                DevPulseNavApp()
            }
        }
    }
}


