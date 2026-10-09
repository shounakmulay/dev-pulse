package dev.shounakmulay.devpulse.core.notifications

data class DPNotificationContent(
    val identifier: DPNotificationIdentifier,
    val channel: DPNotificationChannel,
    @Deprecated("Use TextResource")
    val title: String,
    @Deprecated("Use TextResource")
    val text: String,
    val deeplink: String? = null,
    val progress: DPNotificationProgress? = null,
    val actions: List<DPNotificationAction> = emptyList(),
)
