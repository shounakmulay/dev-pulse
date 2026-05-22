package dev.shounakmulay.devpulse.core.logging

data class DPLogConfig(
    val minLevel: DPLogLevel,
    val sinks: List<LogSink> = emptyList(),
)

object DPLogConfigs {
    fun debug(): DPLogConfig = DPLogConfig(
        minLevel = DPLogLevel.Verbose,
    )

    fun release(): DPLogConfig = DPLogConfig(
        minLevel = DPLogLevel.Info,
    )
}
