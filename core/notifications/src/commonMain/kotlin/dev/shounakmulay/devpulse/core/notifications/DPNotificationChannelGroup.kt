package dev.shounakmulay.devpulse.core.notifications

import devpulse.core.resources.generated.resources.Res
import devpulse.core.resources.generated.resources.feed_data_sync_group_name
import org.jetbrains.compose.resources.StringResource

enum class DPNotificationChannelGroup(val id: String, val groupName: StringResource) {
    FEED("FEED", Res.string.feed_data_sync_group_name)
}
