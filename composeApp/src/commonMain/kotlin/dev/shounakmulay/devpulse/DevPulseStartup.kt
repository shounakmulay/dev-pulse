package dev.shounakmulay.devpulse

import dev.shounakmulay.devpulse.di.koinConfiguration
import dev.shounakmulay.devpulse.logging.DevPulseLogging
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes

object DevPulseStartup {
    fun initialize() {
        initialize {}
    }

    fun initialize(platformConfiguration: KoinAppDeclaration) {
        DevPulseLogging.configure()
        startKoin {
            platformConfiguration()
            includes(koinConfiguration)
        }
    }
}
