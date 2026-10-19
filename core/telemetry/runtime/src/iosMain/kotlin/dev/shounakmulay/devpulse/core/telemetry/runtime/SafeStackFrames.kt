package dev.shounakmulay.devpulse.core.telemetry.runtime

import dev.shounakmulay.devpulse.core.telemetry.api.SafeStackFrame
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

private val functionSymbol = Regex("kfun:([A-Za-z0-9_.$<>-]+)")

@OptIn(kotlin.experimental.ExperimentalNativeApi::class)
internal actual fun safeStackFrames(error: Throwable, maximum: Int): PersistentList<SafeStackFrame> =
    error.getStackTrace().asSequence().take(maximum).mapNotNull { frame ->
        val symbol = functionSymbol.find(frame)?.groupValues?.get(1) ?: return@mapNotNull null
        SafeStackFrame(
            owner = symbol.substringBeforeLast('.', "").take(96),
            method = symbol.substringAfterLast('.').take(64),
            file = "",
            line = 0,
        )
    }.toPersistentList()
