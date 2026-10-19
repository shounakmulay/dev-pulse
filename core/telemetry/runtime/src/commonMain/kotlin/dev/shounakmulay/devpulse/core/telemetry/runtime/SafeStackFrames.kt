package dev.shounakmulay.devpulse.core.telemetry.runtime

import dev.shounakmulay.devpulse.core.telemetry.api.SafeStackFrame
import kotlinx.collections.immutable.PersistentList

internal expect fun safeStackFrames(error: Throwable, maximum: Int): PersistentList<SafeStackFrame>
