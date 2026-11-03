package dev.shounakmulay.devpulse.feature.settings.screens.dataSync.ui

import dev.shounakmulay.devpulse.core.ui.event.ScreenEvent

sealed interface DataSyncScreenEvent : ScreenEvent {
    data object RefreshDiscovery : DataSyncScreenEvent
}
