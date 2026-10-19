package dev.shounakmulay.devpulse.core.telemetry.runtime

internal expect class TelemetryLock() {
    fun <T> withLock(block: () -> T): T
}
