package dev.shounakmulay.devpulse.di

import dev.shounakmulay.devpulse.core.logging.di.nativeLoggingModule
import dev.shounakmulay.devpulse.core.sync.di.nativeSyncModule
import org.koin.core.KoinApplication
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.koinConfiguration

val koinConfiguration: KoinConfiguration = koinConfiguration {
    generatedKoinConfiguration.invoke().invoke(this)
    registerNativeKoinDependencies()
    modules(
        nativeSyncModule(),
        nativeLoggingModule()
    )
}

expect fun KoinApplication.registerNativeKoinDependencies()