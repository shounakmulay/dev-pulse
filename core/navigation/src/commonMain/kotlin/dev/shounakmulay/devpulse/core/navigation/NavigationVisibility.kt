package dev.shounakmulay.devpulse.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlin.uuid.Uuid

enum class NavigationPaneRole { Primary, List, Detail }

interface NavigationVisibilityObserver {
    fun onVisible(entryId: String, screen: Screen, role: NavigationPaneRole)
    fun onHidden(entryId: String)
}

object NoOpNavigationVisibilityObserver : NavigationVisibilityObserver {
    override fun onVisible(entryId: String, screen: Screen, role: NavigationPaneRole) = Unit
    override fun onHidden(entryId: String) = Unit
}

internal data class NavigationPaneVisibility(
    val visible: Boolean = true,
    val role: NavigationPaneRole = NavigationPaneRole.Primary,
)

internal val LocalNavigationPaneVisibility = staticCompositionLocalOf { NavigationPaneVisibility() }

@Composable
internal fun NavigationEntryVisibility(
    screen: Screen,
    observer: NavigationVisibilityObserver,
    content: @Composable () -> Unit,
) {
    val entryId = remember(screen) { Uuid.random().toString() }
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()
    val pane = LocalNavigationPaneVisibility.current
    val visible = pane.visible && lifecycleState.isAtLeast(Lifecycle.State.RESUMED)
    LaunchedEffect(entryId, visible, pane.role, observer) {
        if (visible) observer.onVisible(entryId, screen, pane.role)
    }
    DisposableEffect(entryId, pane.visible, pane.role, observer) {
        onDispose { if (pane.visible) observer.onHidden(entryId) }
    }
    content()
}
