package dev.shounakmulay.devpulse.di

import dev.shounakmulay.devpulse.bridge.markdownconverter.di.MarkdownConverterModule
import dev.shounakmulay.devpulse.core.common.di.CoreCommonModule
import dev.shounakmulay.devpulse.core.data.db.di.DatabaseModule
import dev.shounakmulay.devpulse.core.data.db.di.DatabasePlatformModule
import dev.shounakmulay.devpulse.core.data.feed.di.FeedDataModule
import dev.shounakmulay.devpulse.core.data.settings.di.SettingsDataModule
import dev.shounakmulay.devpulse.core.domain.feed.di.DomainFeedModule
import dev.shounakmulay.devpulse.core.domain.settings.di.DomainSettingsModule
import dev.shounakmulay.devpulse.core.logging.di.LoggingModule
import dev.shounakmulay.devpulse.core.network.di.NetworkModule
import dev.shounakmulay.devpulse.core.preferences.di.PreferencesModule
import dev.shounakmulay.devpulse.core.ui.di.CoreUIModule
import dev.shounakmulay.devpulse.feature.feed.di.FeedModule
import dev.shounakmulay.devpulse.feature.home.di.HomeModule
import dev.shounakmulay.devpulse.feature.settings.di.SettingsModule
import dev.shounakmulay.devpulse.readability.di.ReadabilityModule
import org.koin.core.annotation.KoinApplication
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.koinConfiguration
import org.koin.plugin.module.dsl.koinConfiguration as generatedKoinConfiguration

@KoinApplication(
    modules = [
        CoreCommonModule::class,
        LoggingModule::class,
        NetworkModule::class,
        ComposeAppModule::class,
        SettingsDataModule::class,
        DomainSettingsModule::class,
        HomeModule::class,
        SettingsModule::class,
        PreferencesModule::class,
        DatabasePlatformModule::class,
        DatabaseModule::class,
        FeedModule::class,
        FeedDataModule::class,
        DomainFeedModule::class,
        CoreUIModule::class,
        MarkdownConverterModule::class,
        ReadabilityModule::class
    ]
)
class DevPulseKoinApplication

private val generatedKoinConfiguration = generatedKoinConfiguration<DevPulseKoinApplication>()

val koinConfiguration: KoinConfiguration = koinConfiguration {
    generatedKoinConfiguration.invoke().invoke(this)
}
