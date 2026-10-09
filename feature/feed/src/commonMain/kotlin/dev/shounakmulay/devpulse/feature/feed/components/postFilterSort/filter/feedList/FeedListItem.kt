package dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.filter.feedList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.components.DPCheckbox
import dev.shounakmulay.devpulse.core.designsystem.components.DPClickableRow
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.ui.image.DPFeedImage
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import dev.shounakmulay.devpulse.core.ui.text.asAnnotatedString
import dev.shounakmulay.devpulse.feature.feed.model.UIFeed

@Composable
internal fun FeedListItem(
    onEvent: (FeedListBottomSheetEvent) -> Unit,
    feed: UIFeed,
    selected: Boolean
) {
    DPClickableRow(
        onClick = {
            onEvent(
                FeedListBottomSheetEvent.OnFeedToggled(feed.id)
            )
        },
        horizontalArrangement = Arrangement.spacedBy(
            LocalDPSpacing.current.md,
            Alignment.Start
        )
    ) {
        DPCheckbox(
            modifier = Modifier.weight(1f, fill = false),
            checked = selected,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.tertiary,
            ),
            onCheckedChange = {
                onEvent(
                    FeedListBottomSheetEvent.OnFeedToggled(feed.id)
                )
            }
        )
        DPFeedImage(
            modifier = Modifier.size(16.dp),
            url = feed.websiteImageUrl,
            initials = feed.initials,
            feedTitle = feed.title
        )
        val title = feed.searchHighlights?.highlightedName
            ?: feed.searchHighlights?.highlightedTitle
            ?: TextResource.fromText(feed.title)
        DPTextView(
            modifier = Modifier.weight(1f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            text = title.asAnnotatedString(color = MaterialTheme.colorScheme.primary),
            variant = DPTextViewVariant.BodyMedium
        )
    }
}
