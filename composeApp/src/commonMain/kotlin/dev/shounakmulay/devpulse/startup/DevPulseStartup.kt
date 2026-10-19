package dev.shounakmulay.devpulse.startup

import dev.shounakmulay.devpulse.core.common.coroutines.ApplicationScope
import dev.shounakmulay.devpulse.core.common.startup.StartupAsyncInitialiser
import dev.shounakmulay.devpulse.core.logging.DPLog
import dev.shounakmulay.devpulse.core.logging.DPLogConfig
import dev.shounakmulay.devpulse.core.logging.DPLogLevel
import dev.shounakmulay.devpulse.core.telemetry.api.SafeBreadcrumb
import dev.shounakmulay.devpulse.core.telemetry.runtime.SafeLogMapping
import dev.shounakmulay.devpulse.core.telemetry.runtime.TelemetryLogSink
import dev.shounakmulay.devpulse.core.telemetry.api.AppReleaseContext
import dev.shounakmulay.devpulse.core.telemetry.api.ErrorContext
import dev.shounakmulay.devpulse.core.telemetry.api.ErrorReporter
import dev.shounakmulay.devpulse.core.telemetry.api.OperationDefinition
import dev.shounakmulay.devpulse.core.telemetry.api.Tracer
import dev.shounakmulay.devpulse.core.telemetry.runtime.TelemetryRuntime
import dev.shounakmulay.devpulse.di.koinConfiguration
import dev.shounakmulay.devpulse.logging.DevPulseLogging
import dev.shounakmulay.devpulse.observability.AppObservability
import dev.shounakmulay.devpulse.observability.ObservabilityProviders
import dev.shounakmulay.devpulse.observability.observabilityModule
import dev.shounakmulay.devpulse.observability.platformReleaseContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes

object DevPulseStartup {
    private val startupLock = Mutex()
    private var initialized = false

    fun initialize() = initialize(platformConfiguration = {})

    fun initialize(platformConfiguration: KoinAppDeclaration) = initialize(
        release = platformReleaseContext(),
        platformConfiguration = platformConfiguration,
    )

    fun initialize(
        release: AppReleaseContext,
        providers: ObservabilityProviders = ObservabilityProviders(),
        platformConfiguration: KoinAppDeclaration = {},
    ) {
        if (!startupLock.tryLock()) return
        try {
            if (initialized) return
            initializeApplication(release, providers, platformConfiguration)
        } finally {
            startupLock.unlock()
        }
    }

    private fun initializeApplication(
        release: AppReleaseContext,
        providers: ObservabilityProviders,
        platformConfiguration: KoinAppDeclaration,
    ) {
        DevPulseLogging.configure()
        val koinApp = startKoin {
            platformConfiguration()
            includes(koinConfiguration)
            modules(observabilityModule(release, providers))
        }
        initialized = true
        val koin = koinApp.koin
        val runtime = koin.get<TelemetryRuntime>()
        DPLog.configure(DPLogConfig(
            minLevel = DPLogLevel.Info,
            sinks = listOf(TelemetryLogSink(runtime, listOf(
                SafeLogMapping("DevPulseApp", DPLogLevel.Info, SafeBreadcrumb("app_lifecycle")),
                SafeLogMapping("FeedImportFallbackParser", DPLogLevel.Warn, SafeBreadcrumb("feed_parser")),
            ))),
        ))
        val scope = koin.get<ApplicationScope>()
        koin.get<AppObservability>().start(scope)
        val tracer = koin.get<Tracer>()
        val errors = koin.get<ErrorReporter>()
        koin.getAll<StartupAsyncInitialiser>().forEach { initialiser ->
            scope.launch {
                try {
                    tracer.trace(OperationDefinition("app.async_initialize")) {
                        initialiser.initialise()
                    }
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (failure: Exception) {
                    errors.capture(failure, ErrorContext("app_initializer_failed"))
                    DPLog.e("DevPulseApp") { "Application initializer failed" }
                }
            }
        }
    }
}
