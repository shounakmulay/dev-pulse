package dev.shounakmulay.devpulse.core.permissions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.mohamedrejeb.calf.permissions.Notification
import com.mohamedrejeb.calf.permissions.Permission

enum class DPPermissions {
    NOTIFICATIONS;

    fun toCalfPermission(): Permission {
        return when (this) {
            NOTIFICATIONS -> Permission.Notification
        }
    }
}

enum class DPPermissionStatus {
    GRANTED,
    DENIED,
    DENIED_FOREVER_OR_NOT_REQUESTED
}

@Stable
interface DPPermissionState {
    val permission: DPPermissions
    val status: DPPermissionStatus
    fun launchPermissionRequest()
}

@Composable
fun rememberDPPermissionState(dpPermission: DPPermissions): DPPermissionState =
    rememberCalfPermissionState(dpPermission)
