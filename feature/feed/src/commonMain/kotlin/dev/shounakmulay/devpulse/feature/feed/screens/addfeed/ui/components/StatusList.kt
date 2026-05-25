package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import dev.shounakmulay.devpulse.core.designsystem.components.DPCard
import dev.shounakmulay.devpulse.core.designsystem.components.DPCardStyle
import dev.shounakmulay.devpulse.core.designsystem.components.DPCardVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.theme.DPSize
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.designsystem.theme.contentPadding
import dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.model.UIFeedQueueData

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun LazyGridScope.statusList(processingData: List<UIFeedQueueData>) {
    items(
        items = processingData,
        key = { queueData ->
            queueData.addFeedData.id
        }
    ) { queueData ->
        DPCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DPSize.Small.contentPadding())
                .animateItem(),
            variant = DPCardVariant.Default,
            style = DPCardStyle.Elevated,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(LocalDPSpacing.current.lg),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleStatus(queueData.status)
                DPTextView(
                    modifier = Modifier.weight(1f)
                        .padding(horizontal = LocalDPSpacing.current.sm),
                    text = queueData.addFeedData.name.orEmpty().ifBlank {
                        queueData.addFeedData.url
                    },
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    variant = DPTextViewVariant.TitleMediumEmphasized
                )
            }
        }
    }
}