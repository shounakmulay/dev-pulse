package dev.shounakmulay.devpulse.startup

import dev.shounakmulay.devpulse.core.common.coroutines.ApplicationScope
import dev.shounakmulay.devpulse.core.common.startup.StartupAsyncInitialiser
import dev.shounakmulay.devpulse.di.koinConfiguration
import kotlinx.coroutines.launch
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes

object DevPulseStartup {
    fun initialize() {
        initialize(platformConfiguration = {})
    }

    fun initialize(
        platformConfiguration: KoinAppDeclaration,
    ) {
        val koinApp = startKoin {
            platformConfiguration()
            includes(koinConfiguration)
        }

        koinApp.koin.run {
            val applicationScope by inject<ApplicationScope>()
            getAll<StartupAsyncInitialiser>().forEach {
                applicationScope.launch {
                    it.initialise()
                }
            }
        }
    }
}
