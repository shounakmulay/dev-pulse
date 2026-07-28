package dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.filter.feedList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.designsystem.components.DPButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonStyle
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonVariant
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.bottomsheet.DPModalBottomSheetController
import devpulse.core.resources.generated.resources.apply_filter
import devpulse.core.resources.generated.resources.clear
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FilterApplyActions(
    onFilterUpdated: (RssPostFilter) -> Unit,
    filter: RssPostFilter.FeedIds,
    feedListBottomSheetController: DPModalBottomSheetController,
    viewModel: FeedListBottomSheetViewModel
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(
            horizontal = LocalDPSpacing.current.md,
            vertical = LocalDPSpacing.current.sm
        ),
        horizontalArrangement = Arrangement.spacedBy(
            LocalDPSpacing.current.md,
            Alignment.End
        )
    ) {
        val coroutineScope = rememberCoroutineScope()
        DPButton(
            text = stringResource(stringRes.clear),
            style = DPButtonStyle.Text,
            variant = DPButtonVariant.Tertiary
        ) {
            onFilterUpdated(filter.copy(values = emptySet()))
            coroutineScope.launch { feedListBottomSheetController.hide() }
        }
        DPButton(
            text = stringResource(stringRes.apply_filter),
            variant = DPButtonVariant.Tertiary
        ) {
            onFilterUpdated(filter.copy(values = viewModel.state.value.selectedFeedIds))
            coroutineScope.launch { feedListBottomSheetController.hide() }
        }
    }
}