package dev.shounakmulay.devpulse.core.logging.log

data class DPLogConfig(
    val minLevel: DPLogLevel,
    val sinks: List<LogSink> = emptyList(),
)

object DPLogConfigs {
    fun debug(sinks: List<LogSink>): DPLogConfig = DPLogConfig(
        minLevel = DPLogLevel.Verbose,
        sinks = sinks,
    )

    fun release(sinks: List<LogSink>): DPLogConfig = DPLogConfig(
        minLevel = DPLogLevel.Info,
        sinks = sinks,
    )
}
