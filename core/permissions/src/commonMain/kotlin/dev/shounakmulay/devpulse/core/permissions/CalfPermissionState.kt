package dev.shounakmulay.devpulse.core.permissions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.mohamedrejeb.calf.permissions.ExperimentalPermissionsApi
import com.mohamedrejeb.calf.permissions.PermissionState
import com.mohamedrejeb.calf.permissions.PermissionStatus
import com.mohamedrejeb.calf.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun rememberCalfPermissionState(permission: DPPermissions): DPPermissionState {
    val calfState = rememberPermissionState(permission = permission.toCalfPermission())
    return remember(permission, calfState) {
        CalfPermissionState(permission, calfState)
    }
}

@OptIn(ExperimentalPermissionsApi::class)
internal class CalfPermissionState(
    override val permission: DPPermissions,
    private val calfState: PermissionState,
) : DPPermissionState {
    override val status: DPPermissionStatus
        get() = when (val status = calfState.status) {
            PermissionStatus.Granted -> DPPermissionStatus.GRANTED
            is PermissionStatus.Denied if status.shouldShowRationale -> DPPermissionStatus.DENIED
            is PermissionStatus.Denied -> DPPermissionStatus.DENIED_FOREVER_OR_NOT_REQUESTED
        }

    override fun launchPermissionRequest() {
        calfState.launchPermissionRequest()
    }
}
