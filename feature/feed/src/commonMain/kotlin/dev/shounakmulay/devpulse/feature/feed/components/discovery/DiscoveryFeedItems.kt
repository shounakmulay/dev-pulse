package dev.shounakmulay.devpulse.feature.feed.components.discovery

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import dev.shounakmulay.devpulse.core.designsystem.components.DPCircularProgressIndicator
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButtonStyle
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.DPSize
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPContextColors
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.designsystem.theme.iconSize
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
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(LocalDPSpacing.current.lg),
        horizontalArrangement = Arrangement.spacedBy(LocalDPSpacing.current.md),
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
    val displayState = when {
        state == UIDiscoveryFeedImportState.Added || state == UIDiscoveryFeedImportState.Adding -> state
        pending -> UIDiscoveryFeedImportState.Queued
        else -> state
    }
    val label = stringResource(
        when (displayState) {
            UIDiscoveryFeedImportState.Added -> stringRes.discovery_added
            UIDiscoveryFeedImportState.Adding -> stringRes.discovery_adding
            UIDiscoveryFeedImportState.Queued -> stringRes.discovery_queued
            UIDiscoveryFeedImportState.Failed -> stringRes.feed_action_retry
            UIDiscoveryFeedImportState.NotAdded -> stringRes.discovery_add
        },
    )
    Box(
        modifier = Modifier.size(LocalDPSpacing.current.touchTarget),
        contentAlignment = Alignment.Center,
    ) {
        when (displayState) {
            UIDiscoveryFeedImportState.Queued, UIDiscoveryFeedImportState.Adding -> {
                Box(
                    modifier = Modifier.matchParentSize().semantics {
                        contentDescription = label
                        liveRegion = LiveRegionMode.Polite
                        if (displayState == UIDiscoveryFeedImportState.Adding) {
                            progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate
                        }
                    },
                    contentAlignment = Alignment.Center,
                ) {
                    if (displayState == UIDiscoveryFeedImportState.Adding) {
                        DPCircularProgressIndicator(
                            modifier = Modifier.size(DPSize.Small.iconSize())
                                .clearAndSetSemantics { },
                        )
                    } else {
                        Image(
                            imageVector = DPIcons.Pending,
                            contentDescription = null,
                            modifier = Modifier.size(DPSize.Small.iconSize()),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant),
                        )
                    }
                }
            }

            else -> DPIconButton(
                icon = when (displayState) {
                    UIDiscoveryFeedImportState.Added -> DPIcons.Check
                    UIDiscoveryFeedImportState.Failed -> DPIcons.Refresh
                    else -> DPIcons.Add
                },
                contentDescription = label,
                size = DPSize.Small,
                style = DPIconButtonStyle.Filled,
                colors = if (displayState == UIDiscoveryFeedImportState.Added) {
                    IconButtonDefaults.iconButtonColors(
                        disabledContainerColor = LocalDPContextColors.current.successContainer,
                        disabledContentColor = LocalDPContextColors.current.onSuccessContainer,
                    )
                } else {
                    null
                },
                enabled = displayState != UIDiscoveryFeedImportState.Added,
                onClick = onAdd,
            )
        }
    }
}

@DPComponentPreview
@Composable
private fun DiscoveryFeedRowPreview() {
    Preview {
        DiscoveryFeedRow(
            feed = FeedPreviewData.discoveryFeed,
            pending = false,
            onAdd = {},
            onClick = {},
        )
    }
}

@DPComponentPreview
@Composable
private fun DiscoveryFeedImportStatesPreview() {
    Preview {
        Column(verticalArrangement = Arrangement.spacedBy(LocalDPSpacing.current.sm)) {
            UIDiscoveryFeedImportState.entries.forEach { state ->
                DiscoveryFeedImportButton(state = state, pending = false, onAdd = {})
            }
            DiscoveryFeedImportButton(
                state = UIDiscoveryFeedImportState.NotAdded,
                pending = true,
                onAdd = {},
            )
        }
    }
}
