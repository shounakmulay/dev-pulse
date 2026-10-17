package dev.shounakmulay.devpulse.core.notifications

import dev.shounakmulay.devpulse.core.common.startup.StartupAsyncInitialiser
import org.koin.core.annotation.Factory

@Factory
class DPNotificationInitialiser(
    private val notificationProvider: NotificationProvider
): StartupAsyncInitialiser {
    override suspend fun initialise() {
       notificationProvider.initialise()
    }
}