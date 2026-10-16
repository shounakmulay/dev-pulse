package dev.shounakmulay.devpulse.feature.feed.components.discovery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.designsystem.components.DPButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonStyle
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPSectionDivider
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.theme.DPSize
import dev.shounakmulay.devpulse.core.designsystem.theme.DPTheme
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.feature.feed.model.UIDiscoveryCollectionShelf
import dev.shounakmulay.devpulse.feature.feed.model.UIDiscoveryFeed
import devpulse.core.resources.generated.resources.discovery_collection_empty
import devpulse.core.resources.generated.resources.discovery_feed_count
import devpulse.core.resources.generated.resources.view_all
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.DiscoveryCollectionShelf(
    shelf: UIDiscoveryCollectionShelf,
    pendingImportIds: Set<UUID>,
    onViewAll: (UUID) -> Unit,
    onAddFeed: (UUID) -> Unit,
    onFeedClick: (UIDiscoveryFeed) -> Unit
) {
    stickyHeader(shelf.collection.id.value) {
        DPSectionDivider(
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = DPTheme.spacing.lg, vertical = DPTheme.spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    DPTextView(
                        text = shelf.collection.title,
                        variant = DPTextViewVariant.TitleLarge
                    )
                    DPTextView(
                        text = stringResource(
                            stringRes.discovery_feed_count,
                            shelf.collection.feedCount
                        ),
                        variant = DPTextViewVariant.LabelSmall,
                    )
                }
                DPButton(
                    text = stringResource(stringRes.view_all),
                    size = DPSize.Small,
                    variant = DPButtonVariant.Secondary,
                    style = DPButtonStyle.Text
                ) {
                    onViewAll(shelf.collection.id)
                }
            }
        }
    }
    item("${shelf.collection.id} feeds") {
        if (shelf.feeds.isEmpty()) {
            DiscoveryMessage(stringResource(stringRes.discovery_collection_empty))
        } else {
            LazyRow(
                state = rememberSaveable(
                    shelf.collection.id.value,
                    saver = LazyListState.Saver
                ) { LazyListState() },
                contentPadding = PaddingValues(horizontal = DPTheme.spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(DPTheme.spacing.md),
            ) {
                items(shelf.feeds, key = { it.id.value }) { feed ->
                    DiscoveryFeedCard(
                        feed = feed, pending = feed.id in pendingImportIds,
                        onAdd = {
                            onAddFeed(feed.id)
                        },
                        onClick = {
                            onFeedClick(feed)
                        }
                    )
                }
            }
        }
    }
}
