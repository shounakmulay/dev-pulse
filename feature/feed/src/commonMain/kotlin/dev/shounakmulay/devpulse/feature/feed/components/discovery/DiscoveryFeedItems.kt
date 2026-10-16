package dev.shounakmulay.devpulse.feature.feed.components.discovery

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.components.DPButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonStyle
import dev.shounakmulay.devpulse.core.designsystem.components.DPCard
import dev.shounakmulay.devpulse.core.designsystem.components.DPCardStyle
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.DPSize
import dev.shounakmulay.devpulse.core.designsystem.theme.DPTheme
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import dev.shounakmulay.devpulse.core.ui.text.asAnnotatedString
import dev.shounakmulay.devpulse.core.ui.text.orEmpty
import dev.shounakmulay.devpulse.feature.feed.components.feed.DPFeedListItemImage
import dev.shounakmulay.devpulse.feature.feed.model.UIDiscoveryFeed
import dev.shounakmulay.devpulse.feature.feed.model.UIDiscoveryFeedImportState
import devpulse.core.resources.generated.resources.discovery_add
import devpulse.core.resources.generated.resources.discovery_added
import devpulse.core.resources.generated.resources.discovery_adding
import devpulse.core.resources.generated.resources.discovery_queued
import devpulse.core.resources.generated.resources.feed_action_retry
import org.jetbrains.compose.resources.stringResource

@Composable
fun DiscoveryFeedCard(
    feed: UIDiscoveryFeed,
    pending: Boolean,
    onAdd: () -> Unit,
    onClick: () -> Unit
) {
    DPCard(
        modifier = Modifier.width(240.dp),
        style = DPCardStyle.Outlined,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(DPTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(DPTheme.spacing.md),
        ) {
            Row(
                Modifier.height(IntrinsicSize.Min).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DPFeedListItemImage(
                    url = feed.imageUrl,
                    initials = feed.initials,
                    feedTitle = feed.title,
                )
                DiscoveryFeedImportButton(
                    state = feed.importState,
                    pending = pending,
                    onAdd = onAdd
                )
            }
            DiscoveryFeedText(feed = feed, descriptionLines = 2)
        }
    }
}

@Composable
fun DiscoveryFeedRow(
    feed: UIDiscoveryFeed,
    pending: Boolean,
    onAdd: () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(DPTheme.spacing.lg),
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

    DPButton(
        text = stringResource(label),
        style = when (state) {
            UIDiscoveryFeedImportState.Added -> DPButtonStyle.Outlined
            else -> DPButtonStyle.Filled
        },
        size = DPSize.Small,
        enabled = !pending && state in setOf(
            UIDiscoveryFeedImportState.NotAdded,
            UIDiscoveryFeedImportState.Failed
        ),
        leadingIcon = when (state) {
            UIDiscoveryFeedImportState.Added -> DPIcons.Check
            else -> null
        },
        onClick = onAdd,
    )
}
