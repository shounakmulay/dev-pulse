package dev.shounakmulay.devpulse.core.telemetry.runtime

internal actual fun isProcessFatal(failure: Throwable): Boolean = failure is VirtualMachineError || failure is ThreadDeath
