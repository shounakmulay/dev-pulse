package dev.shounakmulay.devpulse.di

import dev.shounakmulay.feature.feed.di.FeedModule
import dev.shounakmulay.feature.home.di.HomeModule
import org.koin.core.annotation.KoinApplication
import org.koin.plugin.module.dsl.koinConfiguration

@KoinApplication(
    modules = [
        HomeModule::class,
        FeedModule::class
    ]
)
class DevPulseKoinApplication

val koinConfiguration = koinConfiguration<DevPulseKoinApplication>()