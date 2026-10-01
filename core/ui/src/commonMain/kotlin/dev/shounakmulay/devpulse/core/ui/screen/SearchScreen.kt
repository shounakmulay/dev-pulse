package dev.shounakmulay.devpulse.core.ui.screen

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.ExpandedFullScreenContainedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.rememberContainedSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.clearAndSetSemantics
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.button.DPBackNavigationIconButton
import dev.shounakmulay.devpulse.core.ui.effect.Effect
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import devpulse.core.resources.generated.resources.feed_search
import devpulse.core.resources.generated.resources.feed_search_back
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.milliseconds

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    FlowPreview::class
)
@Composable
inline fun <STATE : ScreenState, EFFECT : Effect, reified VM : MviViewModel<STATE, EFFECT>> SearchScreen(
    viewModel: VM,
    noinline onQueryChange: (String) -> Unit,
    noinline onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    initialQuery: String = "",
    placeholder: String = stringResource(stringRes.feed_search),
    searchBarState: SearchBarState = rememberContainedSearchBarState(),
    crossinline onEffect: suspend (Effect) -> Unit,
    crossinline floatingActionButton: @Composable STATE.(collapsedFraction: Float) -> Unit = {},
    crossinline searchContent: @Composable ColumnScope.(STATE) -> Unit,
    crossinline content: @Composable BoxScope.(STATE) -> Unit,
) {
    val textFieldState = rememberTextFieldState(initialText = initialQuery)
    val scope = rememberCoroutineScope()
    val currentOnQueryChange = rememberUpdatedState(onQueryChange)
    val closeSearchDescription = stringResource(stringRes.feed_search_back)
    val colors = SearchBarDefaults.appBarWithSearchColors(
        searchBarColors = SearchBarDefaults.containedColors(state = searchBarState),
    )
    val expanded = searchBarState.currentValue == SearchBarValue.Expanded
    val softwareKeyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(textFieldState) {
        snapshotFlow { textFieldState.text.toString() }
            .distinctUntilChanged()
            .debounce(300.milliseconds)
            .collect { currentOnQueryChange.value(it) }
    }

    val inputField: @Composable () -> Unit = {
        SearchBarDefaults.InputField(
            textFieldState = textFieldState,
            searchBarState = searchBarState,
            colors = colors.searchBarColors.inputFieldColors.copy(
                unfocusedTextColor = colors.searchBarColors.inputFieldColors.unfocusedTextColor.copy(
                    alpha = 0.5f
                )
            ),
            onSearch = {
                softwareKeyboardController?.hide()
            },
            placeholder = {
                DPTextView(
                    text = placeholder,
                    variant = DPTextViewVariant.BodyLarge,
                    modifier = Modifier.clearAndSetSemantics {},
                )
            },
            leadingIcon = if (expanded) {
                {
                    DPIconButton(
                        icon = DPIcons.ArrowBack,
                        contentDescription = closeSearchDescription,
                        onClick = {
                            scope.launch {
                                searchBarState.animateToCollapsed()
                            }
                        },
                    )
                }
            } else null,
            trailingIcon = {
                DPIconButton(
                    icon = if (expanded) DPIcons.Close else Icons.Default.Search,
                    contentDescription = if (expanded) closeSearchDescription else placeholder,
                    onClick = {
                        if (expanded) {
                            textFieldState.clearText()
                        } else {
                            scope.launch {
                                searchBarState.animateToExpanded()
                            }
                        }
                    },
                )
            },
        )
    }

    Screen(
        modifier = modifier,
        viewModel = viewModel,
        onEffect = { onEffect(it) },
        topAppBar = {
            AppBarWithSearch(
                modifier = Modifier.fillMaxWidth(),
                state = searchBarState,
                colors = colors,
                inputField = inputField,
                navigationIcon = {
                    DPBackNavigationIconButton { onNavigateBack() }
                }
            )
        },
        floatingActionButton = {
            floatingActionButton(this, 0f)
        },
    ) { state ->
        ExpandedFullScreenContainedSearchBar(
            state = searchBarState,
            inputField = inputField,
            colors = colors.searchBarColors,
        ) {
            searchContent(state)
        }
        content(state)
    }
}
