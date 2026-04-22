package dev.shounakmulay.devpulse.di

import dev.shounakmulay.feature.feed.di.FeedModule
import dev.shounakmulay.feature.home.di.HomeModule
import dev.shounakmulay.feature.settings.di.SettingsModule
import org.koin.core.annotation.KoinApplication
import org.koin.plugin.module.dsl.koinConfiguration

@KoinApplication(
    modules = [
        HomeModule::class,
        FeedModule::class,
        SettingsModule::class
    ]
)
class DevPulseKoinApplication

val koinConfiguration = koinConfiguration<DevPulseKoinApplication>()