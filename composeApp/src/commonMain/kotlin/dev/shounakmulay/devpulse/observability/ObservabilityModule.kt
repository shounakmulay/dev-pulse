package dev.shounakmulay.devpulse.observability

import dev.shounakmulay.devpulse.core.remoteconfig.ConfigContext
import dev.shounakmulay.devpulse.core.remoteconfig.RemoteConfig
import dev.shounakmulay.devpulse.core.telemetry.api.Analytics
import dev.shounakmulay.devpulse.core.telemetry.api.AppReleaseContext
import dev.shounakmulay.devpulse.core.telemetry.api.ErrorReporter
import dev.shounakmulay.devpulse.core.telemetry.api.Metrics
import dev.shounakmulay.devpulse.core.telemetry.api.TelemetryControl
import dev.shounakmulay.devpulse.core.telemetry.api.TelemetryInspector
import dev.shounakmulay.devpulse.core.telemetry.api.TelemetryLifecycle
import dev.shounakmulay.devpulse.core.telemetry.api.Tracer
import dev.shounakmulay.devpulse.core.telemetry.runtime.TelemetryRuntime
import dev.shounakmulay.devpulse.core.telemetry.runtime.TelemetryRuntimeConfig
import org.koin.dsl.module

internal fun observabilityModule(release: AppReleaseContext, providers: ObservabilityProviders) = module {
    single { release }
    single { providers }
    single {
        TelemetryRuntime(
            release = release,
            providers = providers.telemetry,
            config = TelemetryRuntimeConfig(traceSampleRate = 0.1),
        )
    }
    single<Analytics> { get<TelemetryRuntime>() }
    single<ErrorReporter> { get<TelemetryRuntime>() }
    single<Tracer> { get<TelemetryRuntime>() }
    single<Metrics> { get<TelemetryRuntime>() }
    single<TelemetryControl> { get<TelemetryRuntime>() }
    single<TelemetryInspector> { get<TelemetryRuntime>() }
    single<TelemetryLifecycle> { get<TelemetryRuntime>() }
    single {
        RemoteConfig(
            catalog = AppConfigKeys.catalog,
            store = PreferencesConfigStore(get()),
            initialContext = ConfigContext(
                environment = release.environment,
                attributes = mapOf(
                    "platform" to release.platform,
                    "app_version" to release.appVersion,
                    "channel" to release.channel,
                ),
            ),
            providers = providers.config,
        )
    }
    single { AppObservability(get(), get(), get(), get()) }
    single { AppNavigationTelemetry(get(), get(), get(), get()) }
}
