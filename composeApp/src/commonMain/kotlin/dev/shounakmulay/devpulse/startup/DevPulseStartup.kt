package dev.shounakmulay.devpulse.startup

import androidx.compose.runtime.LaunchedEffect
import dev.shounakmulay.devpulse.core.common.coroutines.ApplicationScope
import dev.shounakmulay.devpulse.core.common.startup.StartupAsyncInitialiser
import dev.shounakmulay.devpulse.core.logging.DPLogger
import dev.shounakmulay.devpulse.core.notifications.NotificationProvider
import dev.shounakmulay.devpulse.di.koinConfiguration
import dev.shounakmulay.devpulse.logging.DevPulseLogging
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes

object DevPulseStartup {
    fun initialize() {
        initialize {}
    }

    fun initialize(platformConfiguration: KoinAppDeclaration) {
        DevPulseLogging.configure()
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
