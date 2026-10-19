package dev.shounakmulay.devpulse.observability

import dev.shounakmulay.devpulse.core.remoteconfig.ConfigProvider
import dev.shounakmulay.devpulse.core.telemetry.api.TelemetryProvider

class ObservabilityProviders(
    telemetry: List<TelemetryProvider> = emptyList(),
    config: List<ConfigProvider> = emptyList(),
) {
    val telemetry = telemetry.toList()
    val config = config.toList()
}
