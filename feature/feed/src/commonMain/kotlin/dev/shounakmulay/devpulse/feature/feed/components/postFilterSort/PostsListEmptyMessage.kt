package dev.shounakmulay.devpulse.feature.feed.components.postFilterSort

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
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.content.EmptyContentPlaceholder
import devpulse.core.resources.generated.resources.clear_filters
import devpulse.core.resources.generated.resources.no_posts_for_filter
import devpulse.core.resources.generated.resources.no_posts_for_filter_subtitle
import devpulse.core.resources.generated.resources.post_no_read_matches
import devpulse.core.resources.generated.resources.post_no_unread_matches
import devpulse.core.resources.generated.resources.post_read_filter_empty_hint
import devpulse.core.resources.generated.resources.post_show_all
import org.jetbrains.compose.resources.stringResource

internal fun LazyGridScope.postsListEmptyMessage(
    readStatus: Boolean? = null,
    showAllPosts: () -> Unit = {},
    clearFilters: () -> Unit,
) {
    item(span = { GridItemSpan(maxLineSpan) }) {
        EmptyContentPlaceholder(
            modifier = Modifier.padding(top = 32.dp).animateItem(),
            icon = DPIcons.EmptyList,
        ) {
            DPTextView(
                modifier = Modifier.fillMaxWidth(0.65f),
                textAlign = TextAlign.Center,
                text = stringResource(
                    when (readStatus) {
                        false -> stringRes.post_no_unread_matches
                        true -> stringRes.post_no_read_matches
                        null -> stringRes.no_posts_for_filter
                    }
                ),
                variant = DPTextViewVariant.HeadingSmallEmphasized,
            )
            Spacer(Modifier.size(LocalDPSpacing.current.sm))
            DPTextView(
                modifier = Modifier.fillMaxWidth(0.75f),
                textAlign = TextAlign.Center,
                text = stringResource(
                    if (readStatus == null) stringRes.no_posts_for_filter_subtitle
                    else stringRes.post_read_filter_empty_hint
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                variant = DPTextViewVariant.TitleSmall,
            )
            Spacer(Modifier.size(LocalDPSpacing.current.xl))
            DPButton(
                text = stringResource(if (readStatus == null) stringRes.clear_filters else stringRes.post_show_all),
                onClick = if (readStatus == null) clearFilters else showAllPosts,
                style = DPButtonStyle.Outlined
            )
        }
    }
}
