package dev.shounakmulay.devpulse.core.notifications

import android.app.PendingIntent
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat

data class AndroidNotificationContent(
    val channel: DPNotificationChannel,
    val title: String,
    val text: String,
    @param:DrawableRes val smallIcon: Int? = null,
    val contentIntent: PendingIntent? = null,
    val actions: List<NotificationCompat.Action> = emptyList(),
) {
    companion object {
        fun from(content: DPNotificationContent): AndroidNotificationContent {
            return AndroidNotificationContent(
                channel = content.channel,
                title = content.title,
                text = content.text,
                contentIntent = null,
                actions = emptyList(),
            )
        }
    }
}
