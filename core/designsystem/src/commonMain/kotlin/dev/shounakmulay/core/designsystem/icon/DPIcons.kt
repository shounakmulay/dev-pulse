package dev.shounakmulay.core.designsystem.icon

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector

object DPIcons {

    @Composable
    fun devPulseIconLarge(): ImageVector {
        val isDarkMode = isSystemInDarkTheme()
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
        val isDarkMode = isSystemInDarkTheme()
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