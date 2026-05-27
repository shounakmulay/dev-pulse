package dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.designsystem.components.DPSegmentedButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPSegmentedVariant
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.model.UISelectedTab
import devpulse.core.resources.generated.resources.feed_tab_all
import devpulse.core.resources.generated.resources.feed_tab_pinned
import org.jetbrains.compose.resources.stringResource

@Composable
fun FilterTabs(
    selectedTab: UISelectedTab,
    onClick: (UISelectedTab) -> Unit
) {
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.padding(horizontal = LocalDPSpacing.current.lg)
    ) {
        UISelectedTab.entries.forEachIndexed { index, tab ->
            DPSegmentedButton(
                variant = DPSegmentedVariant.Secondary,
                selected = tab == selectedTab,
                onClick = {
                    onClick(tab)
                },
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = UISelectedTab.entries.size
                ),
                text = when (tab) {
                    UISelectedTab.ALL -> stringResource(stringRes.feed_tab_all)
                    UISelectedTab.PINNED -> stringResource(stringRes.feed_tab_pinned)
                }
            )
        }
    }
}
