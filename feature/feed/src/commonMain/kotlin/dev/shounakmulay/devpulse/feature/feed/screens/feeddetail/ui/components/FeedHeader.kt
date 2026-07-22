package dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import dev.shounakmulay.devpulse.core.ui.image.DPFeedImage
import dev.shounakmulay.devpulse.core.ui.transition.sharedElement
import dev.shounakmulay.devpulse.feature.feed.screens.model.UIFeed

@Composable
@OptIn(ExperimentalLayoutApi::class)
internal fun FeedHeader(
    uiFeed: UIFeed,
    feed: RssFeed
) {
    Column {
        FlowRow(
            modifier = Modifier
                .padding(LocalDPSpacing.current.md),
            horizontalArrangement = Arrangement.spacedBy(
                LocalDPSpacing.current.xl,
                alignment = Alignment.CenterHorizontally
            ),
            verticalArrangement = Arrangement.spacedBy(
                LocalDPSpacing.current.xl,
                Alignment.CenterVertically
            )
        ) {
            Box(
                Modifier.fillMaxRowHeight(),
                contentAlignment = Alignment.Center
            ) {
                DPFeedImage(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                    url = uiFeed.websiteImageUrl,
                    initials = uiFeed.initials,
                    feedTitle = uiFeed.title
                )
            }
            Box(
                modifier = Modifier.fillMaxRowHeight().weight(1f)
                    .widthIn(min = 350.dp),
                contentAlignment = Alignment.Center
            ) {
                DPTextView(
                    text = uiFeed.title,
                    variant = DPTextViewVariant.HeadingMediumEmphasized,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        feed.description?.let { description ->
            var expanded by rememberSaveable { mutableStateOf(false) }
            DPTextView(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .align(Alignment.CenterHorizontally)
                    .animateContentSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { expanded = !expanded }
                    ),
                text = description,
                maxLines = if (expanded) Int.MAX_VALUE else 2,
                variant = DPTextViewVariant.BodyMedium,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}