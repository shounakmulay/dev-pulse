package dev.shounakmulay.devpulse.core.telemetry.runtime

import dev.shounakmulay.devpulse.core.telemetry.api.SafeStackFrame
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

internal actual fun safeStackFrames(error: Throwable, maximum: Int): PersistentList<SafeStackFrame> =
    error.stackTrace.take(maximum).map { frame ->
        SafeStackFrame(
            owner = frame.className.safeSymbol(96),
            method = frame.methodName.safeSymbol(64),
            file = frame.fileName.orEmpty().safeSymbol(64),
            line = frame.lineNumber.coerceAtLeast(0),
        )
    }.toPersistentList()

private fun String.safeSymbol(maximum: Int): String = take(maximum).filter {
    it.isLetterOrDigit() || it in "_.$<>-"
}
