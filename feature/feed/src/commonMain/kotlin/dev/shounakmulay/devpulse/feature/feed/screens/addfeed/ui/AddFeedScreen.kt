package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.components.DPButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonStyle
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPTopAppBar
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.button.DPBackNavigationIconButton
import dev.shounakmulay.devpulse.core.ui.screen.Screen
import dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.components.AddFeedInputCard
import dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.components.AddNewCardButton
import devpulse.core.resources.generated.resources.add_feed_title
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
        val isList by remember(it.addFeedDataList.size) {
            derivedStateOf {
                it.addFeedDataList.size > 1
            }
        }
        LazyColumn(
            contentPadding = PaddingValues(bottom = 64.dp)
        ) {
            itemsIndexed(
                it.addFeedDataList,
                key = { _, item ->
                    item.id
                }
            ) { index, addFeedData ->
                AddFeedInputCard(
                    index = index,
                    data = addFeedData,
                    showDelete = isList,
                    onUrlChanged = { string ->
                        viewModel.onEvent(
                            AddFeedScreenEvent.UpdateUrl(
                                id = addFeedData.id,
                                url = string
                            )
                        )
                    },
                    onTitleChanged = { string ->
                        viewModel.onEvent(
                            AddFeedScreenEvent.UpdateName(
                                id = addFeedData.id,
                                name = string
                            )
                        )
                    },
                    onDelete = {
                        viewModel.onEvent(
                            AddFeedScreenEvent.Delete(addFeedData.id)
                        )
                    },
                    canToggleExpanded = isList,
                    onToggleExpanded = {
                        viewModel.onEvent(AddFeedScreenEvent.ToggleExpanded(addFeedData.id))
                    },
                    onSourceInputFocusLost = {
                        viewModel.onEvent(AddFeedScreenEvent.ValidateSourceUrl(addFeedData.id))
                    }
                )
            }
            item(key = "ADD") {
                AddNewCardButton {
                    viewModel.onEvent(
                        AddFeedScreenEvent.Add
                    )
                }
            }
        }

        DPButton(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(0.5f)
                .padding(
                    vertical = LocalDPSpacing.current.md,
                    horizontal = LocalDPSpacing.current.md
                ),
            text = "Import",
            variant = DPButtonVariant.Primary,
            style = DPButtonStyle.Elevated,
            elevation = ButtonDefaults.elevatedButtonElevation(),
            onClick = {
                viewModel.onEvent(AddFeedScreenEvent.ImportFeeds)
            }
        )
    }
}
