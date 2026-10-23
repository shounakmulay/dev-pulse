package dev.shounakmulay.devpulse.core.logging.log

import co.touchlab.kermit.Severity

enum class DPLogLevel {
    Verbose,
    Debug,
    Info,
    Warn,
    Error,
    Assert,
}

internal fun DPLogLevel.toSeverity(): Severity = when (this) {
    DPLogLevel.Verbose -> Severity.Verbose
    DPLogLevel.Debug -> Severity.Debug
    DPLogLevel.Info -> Severity.Info
    DPLogLevel.Warn -> Severity.Warn
    DPLogLevel.Error -> Severity.Error
    DPLogLevel.Assert -> Severity.Assert
}
