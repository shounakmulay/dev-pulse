package dev.shounakmulay.devpulse.core.designsystem.icon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import dev.shounakmulay.devpulse.core.designsystem.theme.DPTheme

object DPIcons {

    @Composable
    fun devPulseIconLarge(): ImageVector {
        val isDarkMode = DPTheme.isDarkTheme
        return remember(isDarkMode) {
            if (isDarkMode) {
                DevPulseIconLargeLight
            } else {
                DevPulseIconLargeDark
            }
        }
    }

    @Composable
    fun devPulseIconSmall(): ImageVector {
        val isDarkMode = DPTheme.isDarkTheme
        return remember(isDarkMode) {
            if (isDarkMode) {
                DevPulseIconSmallLight
            } else {
                DevPulseIconSmallDark
            }
        }
    }

    val UserSettings = UserSettingsIcon
}
