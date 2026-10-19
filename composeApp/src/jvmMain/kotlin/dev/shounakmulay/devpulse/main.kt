package dev.shounakmulay.devpulse

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import dev.shounakmulay.devpulse.core.telemetry.api.TelemetryLifecycle
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.compose.koinInject
import kotlin.time.Duration.Companion.seconds
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.shounakmulay.devpulse.startup.DevPulseStartup

fun main() {
    DevPulseStartup.initialize()
    application {
        val scope = rememberCoroutineScope()
        val closing = remember { mutableStateOf(false) }
        val telemetry = koinInject<TelemetryLifecycle>()
        Window(
            onCloseRequest = {
                if (!closing.value) {
                    closing.value = true
                    scope.launch {
                        try {
                            withTimeoutOrNull(2.seconds) {
                                telemetry.flush(1.seconds)
                                telemetry.close(1.seconds)
                            }
                        } finally {
                            exitApplication()
                        }
                    }
                }
            },
            title = "DevPulse",
        ) {
            App()
        }
    }
}
