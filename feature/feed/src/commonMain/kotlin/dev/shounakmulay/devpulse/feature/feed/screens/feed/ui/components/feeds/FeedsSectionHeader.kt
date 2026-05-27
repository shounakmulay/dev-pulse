package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.feeds

import androidx.compose.runtime.Composable
import dev.shounakmulay.devpulse.core.designsystem.components.DPButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonStyle
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPSectionDivider
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.DPSize
import dev.shounakmulay.devpulse.core.resources.stringRes
import devpulse.core.resources.generated.resources.add_feed_action_import
import devpulse.core.resources.generated.resources.add_feed_feeds
import devpulse.core.resources.generated.resources.feed_view_all
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FeedsSectionHeader(
    onNavigateToAddFeed: () -> Unit,
    onNavigateToFeedList: () -> Unit
) {
    DPSectionDivider(
        title = stringResource(stringRes.add_feed_feeds)
    ) {
        DPButton(
            text = stringResource(stringRes.add_feed_action_import),
            variant = DPButtonVariant.Secondary,
            style = DPButtonStyle.Text,
            leadingIcon = DPIcons.Add,
            size = DPSize.Small,
            onClick = onNavigateToAddFeed
        )
        DPButton(
            text = stringResource(stringRes.feed_view_all),
            variant = DPButtonVariant.Secondary,
            style = DPButtonStyle.Text,
            trailingIcon = DPIcons.ArrowRight,
            size = DPSize.Small,
            onClick = onNavigateToFeedList
        )
    }
}
