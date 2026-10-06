package dev.shounakmulay.devpulse.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import dev.shounakmulay.devpulse.core.logging.DPLogger
import dev.shounakmulay.devpulse.core.notifications.NotificationProvider
import dev.shounakmulay.devpulse.core.permissions.DPPermissions
import dev.shounakmulay.devpulse.core.permissions.rememberDPPermissionState
import dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.FeedSyncViewModel
import org.koin.compose.koinInject

@Composable
fun AppLevelInitialisers() {
    val feedSyncViewModel: FeedSyncViewModel = koinInject()
    val logger = koinInject<DPLogger>().withTag("DevPulseApp")

    LaunchedEffect(Unit) {
        logger.i { "Starting app-level feed queue initialiser" }
        feedSyncViewModel.init()
    }

    val notificationProvider: NotificationProvider = koinInject()

    LaunchedEffect(Unit) {
        logger.i { "Starting app-level notification provider" }
        notificationProvider.initialise()
    }

    val notificationPermissionState = rememberDPPermissionState(DPPermissions.NOTIFICATIONS)
    LaunchedEffect(notificationPermissionState) {
        notificationPermissionState.launchPermissionRequest()
    }
}
