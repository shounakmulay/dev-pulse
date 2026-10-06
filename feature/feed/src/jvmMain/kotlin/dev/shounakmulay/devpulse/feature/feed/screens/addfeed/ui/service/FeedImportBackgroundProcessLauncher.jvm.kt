package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.service

import org.koin.core.annotation.Factory

@Factory
class FeedImportBackgroundProcessLauncherImpl: FeedImportBackgroundProcessLauncher {
    override suspend fun launch() {}
}