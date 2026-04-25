package dev.shounakmulay.devpulse.di

import dev.shounakmulay.devpulse.core.network.di.NetworkModule
import dev.shounakmulay.devpulse.feature.feed.di.FeedModule
import dev.shounakmulay.devpulse.feature.home.di.HomeModule
import dev.shounakmulay.feature.settings.di.SettingsModule
import org.koin.core.annotation.KoinApplication
import org.koin.plugin.module.dsl.koinConfiguration

@KoinApplication(
    modules = [
        NetworkModule::class,
        HomeModule::class,
        FeedModule::class,
        SettingsModule::class
    ]
)
class DevPulseKoinApplication

val koinConfiguration = koinConfiguration<DevPulseKoinApplication>()