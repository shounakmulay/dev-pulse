package dev.shounakmulay.devpulse.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import dev.shounakmulay.devpulse.core.permissions.DPPermissions
import dev.shounakmulay.devpulse.core.permissions.rememberDPPermissionState

@Composable
fun AppLevelUiInitialisers() {

    val notificationPermissionState = rememberDPPermissionState(DPPermissions.NOTIFICATIONS)
    LaunchedEffect(notificationPermissionState) {
        notificationPermissionState.launchPermissionRequest()
    }
}
