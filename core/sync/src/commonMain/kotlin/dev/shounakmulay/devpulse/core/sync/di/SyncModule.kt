package dev.shounakmulay.devpulse.core.sync.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module
@ComponentScan("dev.shounakmulay.devpulse.core.sync")
class SyncModule

expect fun nativeSyncModule(): org.koin.core.module.Module