package dev.shounakmulay.devpulse.core.notifications

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import org.koin.core.annotation.Factory
import dev.shounakmulay.devpulse.core.resources.R as AndroidResourcesR

@Factory
class AndroidNotificationBuilder(
    private val context: Context,
) {

    fun createNotification(notification: DPNotificationContent): Notification {
        return when (notification.progress) {
            null -> createMessageNotification(AndroidNotificationContent.from(notification))
            else -> createProgressNotification(
                AndroidNotificationContent.from(notification),
                notification.progress
            )
        }
    }

    fun createMessageNotification(content: AndroidNotificationContent): Notification =
        createBaseBuilder(content)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content.text))
            .build()

    fun createProgressNotification(
        content: AndroidNotificationContent,
        progress: DPNotificationProgress = DPNotificationProgress.Indeterminate,
    ): Notification {
        val builder = createBaseBuilder(content)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setOngoing(true)
            .setAutoCancel(false)
            .setOnlyAlertOnce(true)

        when (progress) {
            DPNotificationProgress.Indeterminate -> builder.setProgress(
                0,
                0,
                true,
            )

            is DPNotificationProgress.Determinate -> builder.setProgress(
                progress.max,
                progress.current,
                false,
            )
        }

        return builder.build()
    }

    private fun createBaseBuilder(content: AndroidNotificationContent): NotificationCompat.Builder =
        NotificationCompat.Builder(context, content.channel.id)
            .setSmallIcon(content.smallIcon ?: AndroidResourcesR.drawable.ic_launcher_monochrome)
            .setContentTitle(content.title)
            .setContentText(content.text)
            .setContentIntent(content.contentIntent)
            .apply {
                content.actions.forEach(::addAction)
            }
}
