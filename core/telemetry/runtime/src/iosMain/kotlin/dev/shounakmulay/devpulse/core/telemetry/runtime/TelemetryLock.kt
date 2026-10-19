package dev.shounakmulay.devpulse.core.telemetry.runtime

import platform.Foundation.NSRecursiveLock

internal actual class TelemetryLock actual constructor() {
    private val lock = NSRecursiveLock()
    actual fun <T> withLock(block: () -> T): T {
        lock.lock()
        return try { block() } finally { lock.unlock() }
    }
}
