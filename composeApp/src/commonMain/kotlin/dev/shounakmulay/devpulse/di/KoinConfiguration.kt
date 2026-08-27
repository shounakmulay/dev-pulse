package dev.shounakmulay.devpulse.di

import dev.shounakmulay.devpulse.core.sync.di.nativeSyncModule
import org.koin.core.KoinApplication
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.koinConfiguration

val koinConfiguration: KoinConfiguration = koinConfiguration {
    generatedKoinConfiguration.invoke().invoke(this)
    registerNativeDependencies()
    modules(
        nativeSyncModule()
    )
}

expect fun KoinApplication.registerNativeDependencies()