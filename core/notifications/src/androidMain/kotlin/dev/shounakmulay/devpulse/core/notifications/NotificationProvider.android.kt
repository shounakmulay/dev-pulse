package dev.shounakmulay.devpulse.core.notifications

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import org.jetbrains.compose.resources.getString
import org.koin.core.annotation.Factory

@Factory
class AndroidNotificationProvider(
    private val context: Context,
    private val notificationBuilder: AndroidNotificationBuilder
) : NotificationProvider {

    private val notificationManager: NotificationManager
        get() {
            return context.getSystemService(NotificationManager::class.java)
        }

    override suspend fun initialise() {
        notificationManager.createNotificationChannelGroups(
            DPNotificationChannelGroup.entries.map { group ->
                getNotificationChannelGroup(group)
            },
        )

        notificationManager.createNotificationChannels(
            getNotificationChannels(),
        )
    }

    override suspend fun showNotification(notification: DPNotificationContent) {
        val nativeNotification = notificationBuilder.createNotification(notification)
        notificationManager.notify(notification.identifier.id, nativeNotification)
    }

    private suspend fun getNotificationChannelGroup(
        group: DPNotificationChannelGroup,
    ): NotificationChannelGroup {
        return NotificationChannelGroup(
            group.id,
            getString(group.groupName),
        )
    }

    private suspend fun getNotificationChannels(): List<NotificationChannel> {
        return DPNotificationChannel.entries.map { channel ->
            NotificationChannel(
                channel.id,
                getString(channel.channelName),
                channel.importance,
            ).apply {
                group = channel.group.id
            }
        }
    }
}
