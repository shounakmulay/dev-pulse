package dev.shounakmulay.devpulse.core.logging.di

import dev.shounakmulay.devpulse.core.logging.events.DPEvent
import dev.shounakmulay.devpulse.core.logging.log.DPLog
import dev.shounakmulay.devpulse.core.logging.performance.DPPerformance
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("dev.shounakmulay.devpulse.core.logging")
class LoggingModule {

    @Single
    fun provideDPEvent() = DPEvent

    @Single
    fun provideDPLog() = DPLog

    @Single
    fun provideDPPerformance() = DPPerformance
}

expect fun nativeLoggingModule(): org.koin.core.module.Module
