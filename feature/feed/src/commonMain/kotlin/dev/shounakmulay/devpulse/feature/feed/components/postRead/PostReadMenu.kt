package dev.shounakmulay.devpulse.feature.feed.components.postRead

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import dev.shounakmulay.devpulse.core.designsystem.components.DPDropdownMenu
import dev.shounakmulay.devpulse.core.designsystem.components.DPDropdownMenuItem
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButtonVariant
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.DPSize
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.feature.feed.model.UIFeedPost
import devpulse.core.resources.generated.resources.open_in_browser
import devpulse.core.resources.generated.resources.post_mark_read
import devpulse.core.resources.generated.resources.post_mark_unread
import devpulse.core.resources.generated.resources.post_read_options
import devpulse.core.resources.generated.resources.share
import org.jetbrains.compose.resources.stringResource

@Composable
fun PostReadMenu(
    post: UIFeedPost,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onReadChanged: (UUID, Boolean) -> Unit,
    onShare: (() -> Unit)? = null,
    onOpenInBrowser: (() -> Unit)? = null,
) {
    Box {
        DPIconButton(
            icon = DPIcons.MoreOptionsVert,
            variant = DPIconButtonVariant.Tertiary,
            contentDescription = stringResource(stringRes.post_read_options),
            size = DPSize.Small,
            onClick = { onExpandedChange(true) },
        )
        DPDropdownMenu(expanded = expanded, onDismissRequest = { onExpandedChange(false) }) {
            if (onShare != null) {
                DPDropdownMenuItem(
                    text = stringResource(stringRes.share),
                    leadingIcon = DPIcons.Share,
                    onClick = {
                        onExpandedChange(false)
                        onShare()
                    },
                )
            }
            if (onOpenInBrowser != null) {
                DPDropdownMenuItem(
                    text = stringResource(stringRes.open_in_browser),
                    leadingIcon = DPIcons.OpenInBrowser,
                    onClick = {
                        onExpandedChange(false)
                        onOpenInBrowser()
                    },
                )
            }
            DPDropdownMenuItem(
                text = stringResource(if (post.isRead) stringRes.post_mark_unread else stringRes.post_mark_read),
                leadingIcon = if (post.isRead) DPIcons.Unread else DPIcons.Read,
                onClick = {
                    onExpandedChange(false)
                    onReadChanged(post.id, !post.isRead)
                },
            )
        }
    }
}
