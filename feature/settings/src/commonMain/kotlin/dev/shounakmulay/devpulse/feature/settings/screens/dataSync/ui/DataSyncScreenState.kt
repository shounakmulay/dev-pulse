package dev.shounakmulay.devpulse.feature.settings.screens.dataSync.ui

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Immutable
data class DataSyncScreenState(
    @Transient val discoveryRefreshStatus: DiscoveryRefreshStatus = DiscoveryRefreshStatus.Idle,
) : ScreenState

enum class DiscoveryRefreshStatus { Idle, Refreshing, Succeeded, Failed }
