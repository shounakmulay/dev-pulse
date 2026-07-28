package dev.shounakmulay.devpulse.feature.feed.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.components.DPButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonStyle
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.list.EmptyListMessage
import devpulse.core.resources.generated.resources.clear_filters
import devpulse.core.resources.generated.resources.no_posts_for_filter
import devpulse.core.resources.generated.resources.no_posts_for_filter_subtitle
import org.jetbrains.compose.resources.stringResource

internal fun LazyGridScope.postsListEmptyMessage(clearFilters: () -> Unit) {
    item(span = { GridItemSpan(maxLineSpan) }) {

        EmptyListMessage(
            modifier = Modifier.padding(top = 32.dp).animateItem()
        ) {
            DPTextView(
                modifier = Modifier.fillMaxWidth(0.65f),
                textAlign = TextAlign.Center,
                text = stringResource(stringRes.no_posts_for_filter),
                variant = DPTextViewVariant.HeadingSmallEmphasized,
            )
            Spacer(Modifier.size(LocalDPSpacing.current.sm))
            DPTextView(
                modifier = Modifier.fillMaxWidth(0.75f),
                textAlign = TextAlign.Center,
                text = stringResource(stringRes.no_posts_for_filter_subtitle),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                variant = DPTextViewVariant.TitleSmall,
            )
            Spacer(Modifier.size(LocalDPSpacing.current.xl))
            DPButton(
                text = stringResource(stringRes.clear_filters),
                onClick = clearFilters,
                style = DPButtonStyle.Outlined
            )
        }
    }
}