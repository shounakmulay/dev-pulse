package dev.shounakmulay.devpulse.core.notifications

import android.app.NotificationManager
import devpulse.core.resources.generated.resources.Res
import devpulse.core.resources.generated.resources.feed_data_sync_channel_name
import devpulse.core.resources.generated.resources.feed_import_channel_name
import org.jetbrains.compose.resources.StringResource

enum class DPNotificationChannel(
    val id: String,
    val channelName: StringResource,
    val importance: Int,
    val group: DPNotificationChannelGroup,
) {
    FEED_IMPORT(
        id = "FEED_IMPORT",
        channelName = Res.string.feed_import_channel_name,
        importance = NotificationManager.IMPORTANCE_DEFAULT,
        group = DPNotificationChannelGroup.FEED,
    ),
    FEED_DATA_SYNC(
        id = "FEED_DATA_SYNC",
        channelName = Res.string.feed_data_sync_channel_name,
        importance = NotificationManager.IMPORTANCE_LOW,
        group = DPNotificationChannelGroup.FEED,
    ),
}
