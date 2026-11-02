package dev.shounakmulay.devpulse.core.notifications

import org.koin.core.annotation.Factory

@Factory
class AppleNotificationProvider : NotificationProvider {
    override suspend fun initialise() {
    }

    override suspend fun showNotification(notification: DPNotificationContent) {
    }
}
