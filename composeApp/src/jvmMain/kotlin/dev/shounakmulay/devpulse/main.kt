package dev.shounakmulay.devpulse

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.shounakmulay.devpulse.startup.DevPulseStartup

fun main() {
    DevPulseStartup.initialize()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "DevPulse",
        ) {
            App()
        }
    }
}
