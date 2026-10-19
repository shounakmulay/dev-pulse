package dev.shounakmulay.devpulse.core.telemetry.runtime

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

internal actual class TelemetryLock actual constructor() {
    private val lock = ReentrantLock()
    actual fun <T> withLock(block: () -> T): T = lock.withLock(block)
}
