package dev.shounakmulay.devpulse.core.notifications

import dev.shounakmulay.devpulse.core.common.startup.StartupAsyncInitialiser
import dev.shounakmulay.devpulse.core.common.startup.StartupPriority
import org.koin.core.annotation.Factory

@Factory
class DPNotificationInitialiser(
    private val notificationProvider: NotificationProvider,
) : StartupAsyncInitialiser {
    override val priority: StartupPriority
        get() = StartupPriority.MEDIUM

    override suspend fun initialise() {
        notificationProvider.initialise()
    }
}
