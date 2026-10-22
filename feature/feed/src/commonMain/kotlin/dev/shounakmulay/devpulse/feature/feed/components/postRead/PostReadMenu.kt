package dev.shounakmulay.devpulse.feature.feed.components.postRead

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.shounakmulay.devpulse.core.designsystem.components.DPDropdownMenu
import dev.shounakmulay.devpulse.core.designsystem.components.DPDropdownMenuItem
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButton
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.DPSize
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.feature.feed.model.UIFeedPost
import devpulse.core.resources.generated.resources.post_mark_read
import devpulse.core.resources.generated.resources.post_mark_unread
import devpulse.core.resources.generated.resources.post_read_options
import org.jetbrains.compose.resources.stringResource

@Composable
fun PostReadMenu(post: UIFeedPost, onReadChanged: (UUID, Boolean) -> Unit) {
    var expanded by remember(post.id) { mutableStateOf(false) }
    Box {
        DPIconButton(
            icon = DPIcons.MoreOptionsVert,
            contentDescription = stringResource(stringRes.post_read_options),
            size = DPSize.Small,
            onClick = { expanded = true },
        )
        DPDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DPDropdownMenuItem(
                text = stringResource(if (post.isRead) stringRes.post_mark_unread else stringRes.post_mark_read),
                onClick = {
                    expanded = false
                    onReadChanged(post.id, !post.isRead)
                },
            )
        }
    }
}
