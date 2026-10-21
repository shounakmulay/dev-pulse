package dev.shounakmulay.devpulse.core.ui.feedback

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Stable
class SnackbarController(
    private val hostState: SnackbarHostState,
    private val coroutineScope: CoroutineScope,
) {
    fun showSnackbar(message: String) {
        coroutineScope.launch { hostState.showSnackbar(message) }
    }
}

val LocalSnackbarController = staticCompositionLocalOf<SnackbarController> {
    error("SnackbarController is not provided")
}
