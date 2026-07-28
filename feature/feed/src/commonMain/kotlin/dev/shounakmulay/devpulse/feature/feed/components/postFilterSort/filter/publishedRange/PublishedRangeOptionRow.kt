package dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.filter.publishedRange

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import dev.shounakmulay.devpulse.core.designsystem.components.DPClickableRow
import dev.shounakmulay.devpulse.core.designsystem.components.DPRadioButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.text.asString
import devpulse.core.resources.generated.resources.custom_min_max

@Composable
internal fun PublishedRangeOptionRow(
    publishedRange: UIPublishedRange,
    onShowDatePicker: () -> Unit,
    onFilterUpdated: (UIPublishedRange) -> Unit,
    state: PublishedRangeFilterState,
    filter: RssPostFilter.PublishedRange
) {
    DPClickableRow(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            if (publishedRange is UIPublishedRange.Custom) {
                onShowDatePicker()
            } else {
                onFilterUpdated(publishedRange)
            }
        },
        horizontalArrangement = Arrangement.spacedBy(
            LocalDPSpacing.current.md,
            Alignment.Start
        )
    ) {
        DPRadioButton(
            modifier = Modifier,
            selected = publishedRange == state.selectedRange,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.tertiary,
            ),
            onClick = {
                if (publishedRange is UIPublishedRange.Custom) {
                    onShowDatePicker()
                } else {
                    onFilterUpdated(publishedRange)
                }
            }
        )

        val title = remember(publishedRange) {
            val publishedRangeSelected =
                publishedRange is UIPublishedRange.Custom && state.selectedRange is UIPublishedRange.Custom
            if (publishedRangeSelected) {
                return@remember PublishedRangeFilterViewModel.publishedRangeTextResource(
                    filter,
                    res = stringRes.custom_min_max
                )
                    ?: publishedRange.title
            }

            publishedRange.title
        }

        DPTextView(
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            text = title.asString(),
            variant = DPTextViewVariant.BodyMedium
        )
    }
}