package dev.shounakmulay.devpulse.feature.settings.screens.settings.ui

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.domain.models.post.FeedsPostListItemVariant
import dev.shounakmulay.devpulse.core.domain.models.theme.ThemeMode
import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class SettingsScreenState(
    val feedPostListItemVariant: FeedsPostListItemVariant = FeedsPostListItemVariant.DEFAULT,
    val isBlackMode: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.DEFAULT,
    val syncInBackground: Boolean = true,
) : ScreenState {
    fun canToggleBlackMode(isDarkTheme: Boolean): Boolean =
        when (themeMode) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
            ThemeMode.SYSTEM -> isDarkTheme
        }
}
