package dev.shounakmulay.devpulse.di

import dev.shounakmulay.devpulse.core.common.di.CoreModule
import dev.shounakmulay.devpulse.core.data.settings.di.SettingsDataModule
import dev.shounakmulay.devpulse.core.domain.settings.di.DomainSettingsModule
import dev.shounakmulay.devpulse.core.network.di.NetworkModule
import dev.shounakmulay.devpulse.core.preferences.di.PreferencesModule
import dev.shounakmulay.devpulse.feature.feed.di.FeedModule
import dev.shounakmulay.devpulse.feature.home.di.HomeModule
import dev.shounakmulay.devpulse.feature.settings.di.SettingsModule
import org.koin.core.annotation.KoinApplication
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.koinConfiguration
import org.koin.plugin.module.dsl.koinConfiguration as generatedKoinConfiguration

@KoinApplication(
    modules = [
        CoreModule::class,
        NetworkModule::class,
        ComposeAppModule::class,
        SettingsDataModule::class,
        DomainSettingsModule::class,
        HomeModule::class,
        FeedModule::class,
        SettingsModule::class,
        PreferencesModule::class
    ]
)
class DevPulseKoinApplication

private val generatedKoinConfiguration = generatedKoinConfiguration<DevPulseKoinApplication>()

val koinConfiguration: KoinConfiguration = koinConfiguration {
    generatedKoinConfiguration.invoke().invoke(this)
}
