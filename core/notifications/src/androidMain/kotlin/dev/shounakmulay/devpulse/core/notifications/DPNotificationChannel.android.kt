package dev.shounakmulay.devpulse.core.notifications

import android.app.NotificationManager
import devpulse.core.resources.generated.resources.Res
import devpulse.core.resources.generated.resources.feed_data_sync_channel_name
import devpulse.core.resources.generated.resources.feed_import_channel_name
import org.jetbrains.compose.resources.StringResource

internal val DPNotificationChannel.channelName: StringResource
    get() = when (this) {
        DPNotificationChannel.FEED_IMPORT -> Res.string.feed_import_channel_name
        DPNotificationChannel.FEED_DATA_SYNC -> Res.string.feed_data_sync_channel_name
    }

internal val DPNotificationChannel.importance: Int
    get() = when (this) {
        DPNotificationChannel.FEED_IMPORT -> NotificationManager.IMPORTANCE_DEFAULT
        DPNotificationChannel.FEED_DATA_SYNC -> NotificationManager.IMPORTANCE_LOW
    }

internal val DPNotificationChannel.group: DPNotificationChannelGroup
    get() = DPNotificationChannelGroup.FEED
