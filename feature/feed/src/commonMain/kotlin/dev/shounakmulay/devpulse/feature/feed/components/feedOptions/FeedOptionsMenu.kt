package dev.shounakmulay.devpulse.feature.feed.components.feedOptions

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import dev.shounakmulay.devpulse.core.designsystem.components.DPAlertDialog
import dev.shounakmulay.devpulse.core.designsystem.components.DPDialogVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPDropdownMenu
import dev.shounakmulay.devpulse.core.designsystem.components.DPDropdownMenuItem
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.resources.stringRes
import devpulse.core.resources.generated.resources.cancel
import devpulse.core.resources.generated.resources.confirm_delete_feed_message
import devpulse.core.resources.generated.resources.confirm_delete_feed_title
import devpulse.core.resources.generated.resources.delete
import devpulse.core.resources.generated.resources.pin
import devpulse.core.resources.generated.resources.share
import devpulse.core.resources.generated.resources.unpin
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun BoxScope.FeedOptionsMenu(
    expanded: Boolean,
    menuItems: ImmutableList<FeedOptionsMenuItem>,
    onMenuItemSelected: (FeedOptionsMenuItem) -> Unit,
    onDismissRequest: () -> Unit
) {

    DPDropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
    ) {
        menuItems.forEach { menuItem ->
            val text = when (menuItem) {
                is FeedOptionsMenuItem.Delete -> stringRes.delete
                is FeedOptionsMenuItem.Pin -> if (menuItem.pinned) stringRes.unpin else stringRes.pin
                is FeedOptionsMenuItem.Share -> stringRes.share
            }
            val icon = when (menuItem) {
                is FeedOptionsMenuItem.Delete -> DPIcons.Delete
                is FeedOptionsMenuItem.Pin -> if (menuItem.pinned) DPIcons.Pin else DPIcons.PinOutlined
                is FeedOptionsMenuItem.Share -> DPIcons.Share
            }
            DPDropdownMenuItem(
                text = stringResource(text),
                trailingIcon = icon,
                onClick = { onMenuItemSelected(menuItem) },
            )
        }
    }
}

@Composable
internal fun FeedDeleteConfirmation(
    confirmation: FeedOptionsState.ConfirmingDelete?,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    confirmation ?: return
    DPAlertDialog(
        icon = DPIcons.DeleteForever,
        title = stringResource(stringRes.confirm_delete_feed_title),
        message = stringResource(stringRes.confirm_delete_feed_message, confirmation.option.title),
        variant = DPDialogVariant.Destructive,
        confirmText = stringResource(stringRes.delete),
        dismissText = stringResource(stringRes.cancel),
        onConfirm = onConfirm,
        onDismissRequest = onDismissRequest,
    )
}
