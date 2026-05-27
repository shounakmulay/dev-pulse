package dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButtonStyle
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButtonVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconToggleButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.DPSize
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.image.DPImage
import dev.shounakmulay.devpulse.feature.feed.screens.model.UIFeed
import devpulse.core.resources.generated.resources.feed_action_pin
import devpulse.core.resources.generated.resources.feed_action_unpin
import devpulse.core.resources.generated.resources.feed_image_content_description
import org.jetbrains.compose.resources.stringResource

@Composable
fun LazyGridItemScope.FeedListRow(
    feed: UIFeed,
    onTogglePinned: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().animateItem(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(LocalDPSpacing.current.md),
    ) {
        Surface(
            modifier = Modifier
                .size(LocalDPSpacing.current.listItemHeight)
                .clip(MaterialTheme.shapes.large),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
        ) {
            DPImage(
                modifier = Modifier.fillMaxSize(),
                url = feed.websiteImageUrl.orEmpty(),
                contentDescription = stringResource(stringRes.feed_image_content_description, feed.title),
                fallbackContent = { fallbackModifier ->
                    Box(
                        modifier = fallbackModifier,
                        contentAlignment = Alignment.Center,
                    ) {
                        DPTextView(
                            text = feed.initials,
                            fontWeight = FontWeight.Bold,
                            variant = DPTextViewVariant.LabelLarge,
                        )
                    }
                },
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(LocalDPSpacing.current.xs),
        ) {
            DPTextView(
                text = feed.title,
                variant = DPTextViewVariant.TitleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            DPTextView(
                text = feed.sourceUrl,
                variant = DPTextViewVariant.BodySmall,
                maxLines = 1,
                overflow = TextOverflow.MiddleEllipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        DPIconToggleButton(
            icon = DPIcons.PinOutlined,
            checkedIcon = DPIcons.Pin,
            contentDescription = if (feed.pinned) {
                stringResource(stringRes.feed_action_unpin)
            } else {
                stringResource(stringRes.feed_action_pin)
            },
            checked = feed.pinned,
            onCheckedChange = onTogglePinned,
            style = DPIconButtonStyle.Outlined,
            variant = DPIconButtonVariant.Tertiary,
            size = DPSize.Small,
        )
    }
}
