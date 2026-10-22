package dev.shounakmulay.devpulse.feature.feed.components.discovery

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButtonStyle
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.DPSize
import dev.shounakmulay.devpulse.core.designsystem.theme.DPTheme
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPContextColors
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import dev.shounakmulay.devpulse.core.ui.text.asAnnotatedString
import dev.shounakmulay.devpulse.core.ui.text.orEmpty
import dev.shounakmulay.devpulse.feature.feed.components.feed.DPFeedListItemImage
import dev.shounakmulay.devpulse.feature.feed.model.UIDiscoveryFeed
import dev.shounakmulay.devpulse.feature.feed.model.UIDiscoveryFeedImportState
import dev.shounakmulay.devpulse.feature.feed.preview.FeedPreviewData
import devpulse.core.resources.generated.resources.discovery_add
import devpulse.core.resources.generated.resources.discovery_added
import devpulse.core.resources.generated.resources.discovery_adding
import devpulse.core.resources.generated.resources.discovery_queued
import devpulse.core.resources.generated.resources.feed_action_retry
import org.jetbrains.compose.resources.stringResource

@Composable
fun DiscoveryFeedRow(
    modifier: Modifier = Modifier,
    feed: UIDiscoveryFeed,
    pending: Boolean,
    onAdd: () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(DPTheme.spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(DPTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DPFeedListItemImage(
            url = feed.imageUrl,
            initials = feed.initials,
            feedTitle = feed.title,
        )
        Column(Modifier.weight(1f)) { DiscoveryFeedText(feed = feed, descriptionLines = 1) }
        DiscoveryFeedImportButton(state = feed.importState, pending = pending, onAdd = onAdd)
    }
}

@Composable
private fun ColumnScope.DiscoveryFeedText(feed: UIDiscoveryFeed, descriptionLines: Int) {
    val highlightColor = MaterialTheme.colorScheme.primary
    DPTextView(
        text = (feed.searchHighlights?.title ?: TextResource.fromText(feed.title))
            .asAnnotatedString(color = highlightColor),
        variant = DPTextViewVariant.TitleMedium,
        minLines = 1,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
    val description = feed.searchHighlights?.description ?: feed.description
    DPTextView(
        text = description?.asAnnotatedString(color = highlightColor).orEmpty(),
        variant = DPTextViewVariant.BodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        minLines = descriptionLines,
        maxLines = descriptionLines,
        overflow = TextOverflow.Ellipsis,
    )
    val urlMatch = feed.searchHighlights?.feedUrl ?: feed.searchHighlights?.websiteUrl
    urlMatch?.let {
        DPTextView(
            text = it.asAnnotatedString(color = highlightColor),
            variant = DPTextViewVariant.BodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }

}

@Composable
fun DiscoveryFeedImportButton(
    state: UIDiscoveryFeedImportState,
    pending: Boolean,
    onAdd: () -> Unit,
) {
    val label = when {
        pending -> stringRes.discovery_adding
        state == UIDiscoveryFeedImportState.Added -> stringRes.discovery_added
        state == UIDiscoveryFeedImportState.Adding -> stringRes.discovery_adding
        state == UIDiscoveryFeedImportState.Queued -> stringRes.discovery_queued
        state == UIDiscoveryFeedImportState.Failed -> stringRes.feed_action_retry
        else -> stringRes.discovery_add
    }

    DPIconButton(
        icon = when (state) {
            UIDiscoveryFeedImportState.Added -> DPIcons.Check
            UIDiscoveryFeedImportState.Failed -> DPIcons.Refresh
            else -> DPIcons.Add
        },
        contentDescription = stringResource(label),
        size = DPSize.Small,
        style = DPIconButtonStyle.Filled,
        colors = if (state == UIDiscoveryFeedImportState.Added) IconButtonDefaults.iconButtonColors(
            disabledContainerColor = LocalDPContextColors.current.successContainer,
            disabledContentColor = LocalDPContextColors.current.onSuccessContainer
        ) else null,
        enabled = !pending && state in setOf(
            UIDiscoveryFeedImportState.NotAdded,
            UIDiscoveryFeedImportState.Queued,
            UIDiscoveryFeedImportState.Failed
        ),
        onClick = onAdd,
    )

//    DPButton(
//        text = stringResource(label),
//        style = when (state) {
//            UIDiscoveryFeedImportState.Added -> DPButtonStyle.Outlined
//            else -> DPButtonStyle.Filled
//        },
//        size = DPSize.Small,
//        enabled = !pending && state in setOf(
//            UIDiscoveryFeedImportState.NotAdded,
//            UIDiscoveryFeedImportState.Queued,
//            UIDiscoveryFeedImportState.Failed
//        ),
//        leadingIcon = when (state) {
//            UIDiscoveryFeedImportState.Added -> DPIcons.Check
//            else -> null
//        },
//        onClick = onAdd,
//    )
}

//@DPComponentPreview
//@Composable
//private fun DiscoveryFeedCardPreview() {
//    Preview {
//        DiscoveryFeedCard(
//            feed = FeedPreviewData.discoveryFeed,
//            pending = false,
//            onAdd = {},
//            onClick = {})
//    }
//}

@DPComponentPreview
@Composable
private fun DiscoveryFeedRowPreview() {
    Preview {
        DiscoveryFeedRow(
            feed = FeedPreviewData.discoveryFeed,
            pending = false,
            onAdd = {},
            onClick = {})
    }
}

@DPComponentPreview
@Composable
private fun DiscoveryFeedImportStatesPreview() {
    Preview {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            UIDiscoveryFeedImportState.entries.forEach { state ->
                DiscoveryFeedImportButton(state = state, pending = false, onAdd = {})
            }
            DiscoveryFeedImportButton(
                state = UIDiscoveryFeedImportState.NotAdded,
                pending = true,
                onAdd = {})
        }
    }
}
