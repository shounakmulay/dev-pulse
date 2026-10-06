package dev.shounakmulay.devpulse.core.permissions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.runtime.snapshots.SnapshotStateObserver
import com.mohamedrejeb.calf.permissions.ExperimentalPermissionsApi
import com.mohamedrejeb.calf.permissions.Notification
import com.mohamedrejeb.calf.permissions.Permission
import com.mohamedrejeb.calf.permissions.PermissionState
import com.mohamedrejeb.calf.permissions.PermissionStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalPermissionsApi::class)
class CalfPermissionStateTest {
    @Test
    fun `Given a permission state When its status changes Then DP status reflects each result`() {
        val calfState = FakePermissionState()
        val state: DPPermissionState = CalfPermissionState(DPPermissions.NOTIFICATIONS, mutableStateOf(calfState))

        assertEquals(DPPermissions.NOTIFICATIONS, state.permission)
        assertEquals(DPPermissionStatus.DENIED_FOREVER_OR_NOT_REQUESTED, state.status)

        calfState.status = PermissionStatus.Denied(shouldShowRationale = true)
        assertEquals(DPPermissionStatus.DENIED, state.status)

        calfState.status = PermissionStatus.Granted
        assertEquals(DPPermissionStatus.GRANTED, state.status)

        calfState.status = PermissionStatus.Denied(shouldShowRationale = false)
        assertEquals(DPPermissionStatus.DENIED_FOREVER_OR_NOT_REQUESTED, state.status)
    }

    @Test
    fun `Given an observed DP status When permission changes Then the observer is notified`() {
        val calfState = FakePermissionState()
        val state: DPPermissionState = CalfPermissionState(DPPermissions.NOTIFICATIONS, mutableStateOf(calfState))
        val observer = SnapshotStateObserver { callback -> callback() }
        val observedChanges = mutableListOf<DPPermissionStatus>()
        observer.start()
        try {
            observer.observeReads(
                scope = state,
                onValueChangedForScope = { observedChanges.add(it.status) },
                block = { assertEquals(DPPermissionStatus.DENIED_FOREVER_OR_NOT_REQUESTED, state.status) },
            )

            Snapshot.withMutableSnapshot {
                calfState.status = PermissionStatus.Granted
            }
            Snapshot.sendApplyNotifications()

            assertEquals(listOf(DPPermissionStatus.GRANTED), observedChanges)
        } finally {
            observer.stop()
            observer.clear()
        }
    }

    @Test
    fun `Given a DP state When a request is explicitly launched Then the adapter requests permission`() {
        val calfState = FakePermissionState()
        val state: DPPermissionState = CalfPermissionState(DPPermissions.NOTIFICATIONS, mutableStateOf(calfState))

        assertTrue(calfState.requests.isEmpty())
        state.launchPermissionRequest()

        assertEquals(listOf(Permission.Notification), calfState.requests)
    }

    @Test
    fun `Given a granted DP state When its backend is replaced Then revocation is observed and requests use the new backend`() {
        val original = FakePermissionState().apply { status = PermissionStatus.Granted }
        val current = mutableStateOf<PermissionState>(original)
        val state: DPPermissionState = CalfPermissionState(DPPermissions.NOTIFICATIONS, current)
        val replacement = FakePermissionState()
        val observedChanges = mutableListOf<DPPermissionStatus>()
        val observer = SnapshotStateObserver { callback -> callback() }
        observer.start()
        try {
            observer.observeReads(
                scope = state,
                onValueChangedForScope = { observedChanges.add(it.status) },
                block = { assertEquals(DPPermissionStatus.GRANTED, state.status) },
            )

            Snapshot.withMutableSnapshot {
                current.value = replacement
            }
            Snapshot.sendApplyNotifications()

            assertEquals(listOf(DPPermissionStatus.DENIED_FOREVER_OR_NOT_REQUESTED), observedChanges)
            assertTrue(original.requests.isEmpty())
            assertTrue(replacement.requests.isEmpty())

            state.launchPermissionRequest()
            assertTrue(original.requests.isEmpty())
            assertEquals(listOf(Permission.Notification), replacement.requests)
        } finally {
            observer.stop()
            observer.clear()
        }
    }

    private class FakePermissionState : PermissionState {
        override val permission: Permission = Permission.Notification
        override var status: PermissionStatus by mutableStateOf(PermissionStatus.Denied(shouldShowRationale = false))
        val requests = mutableListOf<Permission>()

        override fun launchPermissionRequest() {
            requests.add(permission)
        }

        override fun openAppSettings() = Unit
    }
}
