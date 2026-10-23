package dev.shounakmulay.devpulse.core.logging.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module
@ComponentScan("dev.shounakmulay.devpulse.core.logging")
class LoggingModule

expect fun nativeLoggingModule() : org.koin.core.module.Module
