package dev.shounakmulay.devpulse.core.logging

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

internal fun Severity.toDPLogLevel(): DPLogLevel = when (this) {
    Severity.Verbose -> DPLogLevel.Verbose
    Severity.Debug -> DPLogLevel.Debug
    Severity.Info -> DPLogLevel.Info
    Severity.Warn -> DPLogLevel.Warn
    Severity.Error -> DPLogLevel.Error
    Severity.Assert -> DPLogLevel.Assert
}
