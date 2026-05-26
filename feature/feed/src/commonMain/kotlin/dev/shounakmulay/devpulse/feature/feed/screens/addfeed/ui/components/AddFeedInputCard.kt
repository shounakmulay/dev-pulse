package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import dev.shounakmulay.devpulse.core.designsystem.components.DPCard
import dev.shounakmulay.devpulse.core.designsystem.components.DPCardStyle
import dev.shounakmulay.devpulse.core.designsystem.components.DPCardVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.text.asString
import dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.model.UIAddFeedData
import devpulse.core.resources.generated.resources.add_feed_action_delete_feed
import org.jetbrains.compose.resources.stringResource

@Composable
fun LazyGridItemScope.AddFeedInputCard(
    index: Int,
    data: UIAddFeedData,
    onUrlChanged: (String) -> Unit,
    onTitleChanged: (String) -> Unit,
    showDelete: Boolean,
    onDelete: () -> Unit,
    canToggleExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    onSourceInputFocusLost: () -> Unit
) {

    DPCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(LocalDPSpacing.current.sm)
            .clickable(enabled = canToggleExpanded, onClick = onToggleExpanded)
            .animateItem(),
        variant = DPCardVariant.Default,
        style = DPCardStyle.Elevated,
    ) {
        Column(
            Modifier.fillMaxWidth().padding(LocalDPSpacing.current.lg)
        ) {
            CardHeader(
                index = index,
                expanded = data.expanded,
                error = data.error != null,
                title = data.collapsedHeaderText.asString(),
                showDelete = showDelete,
                onDelete = onDelete
            )

            CardBody(
                expanded = data.expanded,
                url = data.url,
                onUrlChanged = onUrlChanged,
                title = data.name.orEmpty(),
                error = data.error,
                onTitleChanged = onTitleChanged,
                onSourceInputFocusLost = onSourceInputFocusLost
            )
        }
    }
}

@Composable
private fun ColumnScope.CardBody(
    expanded: Boolean,
    url: String,
    onUrlChanged: (String) -> Unit,
    title: String,
    onTitleChanged: (String) -> Unit,
    onSourceInputFocusLost: () -> Unit,
    error: UIAddFeedData.ValidationError?
) {
    AnimatedVisibility(expanded) {
        Column {
            Spacer(Modifier.size(LocalDPSpacing.current.sm))
            SourceInput(
                url = url,
                error = error,
                onUrlChanged = onUrlChanged,
                onFocusLost = onSourceInputFocusLost
            )
            Spacer(Modifier.size(LocalDPSpacing.current.md))
            NameInput(
                title = title,
                onTitleChanged = onTitleChanged
            )
            Spacer(Modifier.size(LocalDPSpacing.current.md))
        }
    }
}

@Composable
private fun CardHeader(
    index: Int,
    expanded: Boolean,
    error: Boolean,
    title: String,
    showDelete: Boolean,
    onDelete: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CircleNumber(number = index + 1, error = error)

        AnimatedVisibility(!expanded) {
            DPTextView(
                modifier = Modifier.weight(1f),
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                variant = DPTextViewVariant.TitleMediumEmphasized
            )
        }

        AnimatedVisibility(showDelete) {
            DPIconButton(
                icon = DPIcons.Close,
                contentDescription = stringResource(stringRes.add_feed_action_delete_feed),
                onClick = onDelete
            )
        }
    }
}
