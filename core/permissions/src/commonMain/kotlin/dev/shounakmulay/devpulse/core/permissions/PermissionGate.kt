package dev.shounakmulay.devpulse.core.permissions

import androidx.compose.runtime.Composable
import dev.shounakmulay.devpulse.core.permissions.DPPermissionStatus.DENIED
import dev.shounakmulay.devpulse.core.permissions.DPPermissionStatus.DENIED_FOREVER_OR_NOT_REQUESTED
import dev.shounakmulay.devpulse.core.permissions.DPPermissionStatus.GRANTED

@Composable
fun PermissionGate(
    permission: DPPermissions,
    autoRequest: Boolean = true,
    blockedContent: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    val permissionState = rememberDPPermissionState(permission)

    when (permissionState.status) {
        GRANTED -> content()
        DENIED -> blockedContent()
        DENIED_FOREVER_OR_NOT_REQUESTED -> TODO()
    }
}