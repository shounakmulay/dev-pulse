package dev.shounakmulay.devpulse.core.notifications

interface NotificationProvider {
    suspend fun initialise()

    suspend fun showNotification(notification: DPNotificationContent)
}
