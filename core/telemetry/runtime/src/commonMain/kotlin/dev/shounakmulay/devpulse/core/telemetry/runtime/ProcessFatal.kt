package dev.shounakmulay.devpulse.core.telemetry.runtime

internal expect fun isProcessFatal(failure: Throwable): Boolean
