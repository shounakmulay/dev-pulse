package dev.shounakmulay.devpulse.observability

import dev.shounakmulay.devpulse.core.remoteconfig.ConfigCatalog
import dev.shounakmulay.devpulse.core.remoteconfig.ConfigKey

object AppConfigKeys {
    val disableTelemetry = ConfigKey.BooleanKey(
        namespace = "telemetry",
        name = "disabled",
        defaultValue = false,
    )
    val traceSampleRate = ConfigKey.DoubleKey(
        namespace = "telemetry",
        name = "trace_sample_rate",
        defaultValue = 0.1,
        range = 0.0..0.1,
    )
    val catalog = ConfigCatalog(listOf(disableTelemetry, traceSampleRate))
}
