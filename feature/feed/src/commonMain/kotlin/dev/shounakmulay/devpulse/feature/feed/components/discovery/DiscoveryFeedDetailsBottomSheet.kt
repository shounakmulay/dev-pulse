package dev.shounakmulay.devpulse.feature.feed.components.discovery

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
import dev.shounakmulay.devpulse.core.designsystem.theme.DPTheme
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.ui.bottomsheet.DPModalBottomSheet
import dev.shounakmulay.devpulse.core.ui.bottomsheet.rememberDPModalBottomSheetController
import dev.shounakmulay.devpulse.core.ui.image.DPFeedImage
import dev.shounakmulay.devpulse.core.ui.text.asString
import dev.shounakmulay.devpulse.core.ui.text.orEmpty
import dev.shounakmulay.devpulse.feature.feed.model.UIDiscoveryFeed
import dev.shounakmulay.devpulse.feature.feed.preview.FeedPreviewData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoveryFeedDetailsBottomSheet(
    feed: UIDiscoveryFeed,
    pending: Boolean,
    onAddFeed: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val controller = rememberDPModalBottomSheetController(skipPartiallyExpanded = true)
    LaunchedEffect(feed.id) { controller.show() }
    DPModalBottomSheet(
        modifier = modifier,
        controller = controller,
        onDismissRequest = onDismissRequest,
    ) {
        BottomSheetContent(
            feed = feed,
            pending = pending,
            onAddFeed = onAddFeed,
            onDismissRequest = onDismissRequest
        )
    }
}

@Composable
private fun BottomSheetContent(
    feed: UIDiscoveryFeed,
    pending: Boolean,
    onAddFeed: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    Column(
        Modifier.padding(LocalDPSpacing.current.lg)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(LocalDPSpacing.current.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DPFeedImage(
                modifier = Modifier.size(LocalDPSpacing.current.listItemHeight)
                    .clip(MaterialTheme.shapes.large),
                url = feed.imageUrl,
                initials = feed.initials,
                feedTitle = feed.title,
            )
            DPTextView(
                modifier = Modifier.weight(1f),
                text = feed.title,
                variant = DPTextViewVariant.TitleLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            DiscoveryFeedImportButton(
                state = feed.importState,
                pending = pending,
                onAdd = {
                    onAddFeed()
                    onDismissRequest()
                },
            )
        }
        Column(
            Modifier
                .padding(top = LocalDPSpacing.current.xl)
                .verticalScroll(rememberScrollState())
        ) {
            var descriptionExpanded by rememberSaveable(feed.id) {
                mutableStateOf(false)
            }

            DPTextView(
                modifier = Modifier.animateContentSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { descriptionExpanded = !descriptionExpanded },
                text = feed.description.orEmpty().asString(),
                variant = DPTextViewVariant.BodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                overflow = TextOverflow.Ellipsis,
                maxLines = if (descriptionExpanded) Int.MAX_VALUE else 10,
            )
        }
    }
}

@DPComponentPreview
@Composable
private fun BottomSheetContentPreview() {
    Preview {
        Column(
            modifier = Modifier.padding(DPTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(DPTheme.spacing.md),
        ) {
            BottomSheetContent(
                feed = FeedPreviewData.discoveryFeed,
                pending = false,
                onAddFeed = {},
                onDismissRequest = { }
            )
        }
    }
}
