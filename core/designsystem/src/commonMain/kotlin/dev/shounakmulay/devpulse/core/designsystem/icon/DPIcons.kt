package dev.shounakmulay.devpulse.core.designsystem.icon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddToQueue
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import dev.shounakmulay.devpulse.core.designsystem.theme.DPTheme
import androidx.compose.material.icons.outlined.PushPin as PushPinOutlined

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
    val Queued = Icons.Default.AddToQueue
    val Processing = Icons.Default.Sync
    val Completed = Icons.Default.Check
    val Failed = Icons.Default.Error
    val Edit = Icons.Default.Edit
    val Refresh = Icons.Default.Refresh
    val ClearAll = Icons.Default.ClearAll
    val ArrowRight = Icons.AutoMirrored.Default.KeyboardArrowRight
    val Pin = Icons.Filled.PushPin
    val PinOutlined = Icons.Outlined.PushPinOutlined
    val BookmarkAddOutline = Icons.Outlined.BookmarkAdd
    val BookmarkAdded = Icons.Default.BookmarkAdded
}
