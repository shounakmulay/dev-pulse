package dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.filter.publishedRange

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner
import dev.shounakmulay.devpulse.core.designsystem.components.DPButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonStyle
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.bottomsheet.DPModalBottomSheet
import dev.shounakmulay.devpulse.core.ui.bottomsheet.DPModalBottomSheetController
import devpulse.core.resources.generated.resources.clear
import devpulse.core.resources.generated.resources.select_published_range
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun PublishedRangeOptionBottomSheet(
    bottomSheetController: DPModalBottomSheetController,
    onShowDatePicker: () -> Unit,
    onFilterUpdated: (RssPostFilter.PublishedRange) -> Unit,
    filter: RssPostFilter.PublishedRange
) {
    DPModalBottomSheet(controller = bottomSheetController) {
        val scopedViewModelOwner = rememberViewModelStoreOwner()
        CompositionLocalProvider(LocalViewModelStoreOwner provides scopedViewModelOwner) {
            val viewModel: PublishedRangeFilterViewModel = koinViewModel { parametersOf(filter) }
            val state by viewModel.collectAsState()

            viewModel.collectSideEffect {
                when (it) {
                    is PublishedRangeFilterEffect.OnFilterUpdated -> {
                        onFilterUpdated(it.filter)
                    }

                    PublishedRangeFilterEffect.OnShowDatePicker -> {
                        onShowDatePicker()
                    }
                }
            }

            DPTextView(
                modifier = Modifier.padding(
                    horizontal = LocalDPSpacing.current.xl,
                    vertical = LocalDPSpacing.current.xs
                ),
                text = stringResource(stringRes.select_published_range),
                variant = DPTextViewVariant.TitleMedium
            )
            PublishedRangeOptionsList(
                state = state,
                onShowDatePicker = onShowDatePicker,
                onFilterUpdated = {
                    viewModel.onEvent(PublishedRangeFilterEvent.OnFilterUpdated(it))
                },
                filter = filter
            )
            DPButton(
                modifier = Modifier
                    .padding(horizontal = LocalDPSpacing.current.md)
                    .padding(bottom = LocalDPSpacing.current.sm)
                    .align(Alignment.End),
                text = stringResource(stringRes.clear),
                variant = DPButtonVariant.Tertiary,
                style = DPButtonStyle.Text,
                onClick = {
                    onFilterUpdated(RssPostFilter.PublishedRange(null, null))
                }
            )
        }
    }
}