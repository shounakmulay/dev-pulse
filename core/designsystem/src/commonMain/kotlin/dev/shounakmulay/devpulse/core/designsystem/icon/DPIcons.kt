package dev.shounakmulay.devpulse.core.designsystem.icon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RssFeed
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
    val RssFeed = Icons.Default.RssFeed
    val Delete = Icons.Default.Delete
    val Close = Icons.Default.Close
    val Add = Icons.Default.Add
}
