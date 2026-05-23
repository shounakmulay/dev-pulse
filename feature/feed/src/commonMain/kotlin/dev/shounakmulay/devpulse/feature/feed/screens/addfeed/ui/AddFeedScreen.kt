package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.designsystem.components.DPButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPCard
import dev.shounakmulay.devpulse.core.designsystem.components.DPCardStyle
import dev.shounakmulay.devpulse.core.designsystem.components.DPCardVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextField
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPTopAppBar
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.DPSize
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.designsystem.theme.contentPadding
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.button.DPBackNavigationIconButton
import dev.shounakmulay.devpulse.core.ui.screen.Screen
import devpulse.core.resources.generated.resources.add_feed_title
import devpulse.core.resources.generated.resources.feed_number
import org.jetbrains.compose.resources.stringResource

@Composable
fun AddFeedScreen(
    viewModel: AddFeedViewModel,
    navigator: Navigator,
    modifier: Modifier = Modifier,
) {
    Screen(
        viewModel = viewModel,
        topAppBar = {
            DPTopAppBar(
                title = stringResource(stringRes.add_feed_title),
                navigationIcon = {
                    DPBackNavigationIconButton {
                        navigator.navigateBack()
                    }
                }
            )
        },
        onEffect = {

        },
    ) {
        LazyColumn(
            contentPadding = DPSize.Medium.contentPadding()
        ) {
            itemsIndexed(it.addFeedDataList) { index, addFeedData ->
                AddFeedInputCard(
                    index = index,
                    url = addFeedData.url,
                    onUrlChanged = {
                        viewModel.onEvent(
                            AddFeedScreenEvent.UpdateUrl(
                                index = index,
                                url = it
                            )
                        )
                    }
                )
            }
        }

        DPButton(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                .padding(LocalDPSpacing.current.xl),
            text = "Import",
            variant = DPButtonVariant.Primary,
            onClick = {
                viewModel.onEvent(AddFeedScreenEvent.ImportFeeds)
            }
        )
    }
}

@Composable
fun AddFeedInputCard(index: Int, url: String, onUrlChanged: (String) -> Unit) {
    DPCard(
        modifier = Modifier.fillMaxWidth(),
        variant = DPCardVariant.Default, style = DPCardStyle.Elevated,
    ) {
        Column(
            Modifier.fillMaxWidth().padding(LocalDPSpacing.current.sm)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DPTextView(
                    modifier = Modifier.padding(start = LocalDPSpacing.current.sm),
                    text = stringResource(stringRes.feed_number, index + 1),
                    variant = DPTextViewVariant.TitleLargeEmphasized
                )
                DPIconButton(
                    icon = DPIcons.Close,
                    contentDescription = "",
                    onClick = {}
                )
            }

            Spacer(Modifier.size(LocalDPSpacing.current.lg))

            DPTextField(
                modifier = Modifier.fillMaxWidth(),
                value = url,
                onValueChange = onUrlChanged,
            )
        }
    }
}
