package dev.shounakmulay.devpulse.feature.home.screens.home.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.ExpandedFullScreenContainedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberContainedSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import dev.shounakmulay.devpulse.core.designsystem.components.DPClickableRow
import dev.shounakmulay.devpulse.core.designsystem.components.DPHorizontalDivider
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButtonVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.DPTheme
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.ui.screen.Screen
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import dev.shounakmulay.devpulse.core.ui.text.asAnnotatedString
import dev.shounakmulay.devpulse.core.ui.text.textResource
import devpulse.core.resources.generated.resources.Res
import devpulse.core.resources.generated.resources.text_resource_greeting
import devpulse.core.resources.generated.resources.text_resource_greeting_with_name
import devpulse.core.resources.generated.resources.text_resource_item_count
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel, navigator: Navigator) {
    val textFieldState = rememberTextFieldState()
    val searchBarState = rememberContainedSearchBarState()
    val scope = rememberCoroutineScope()
    val appBarWithSearchColors =
        SearchBarDefaults.appBarWithSearchColors(
            searchBarColors = SearchBarDefaults.containedColors(state = searchBarState),
        )
    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                textFieldState = textFieldState,
                searchBarState = searchBarState,
                colors = appBarWithSearchColors.searchBarColors.inputFieldColors,
                onSearch = { },
                placeholder = {
                    Text(modifier = Modifier.clearAndSetSemantics {}, text = "Search", maxLines = 1)
                },
                trailingIcon = {
                    Box(
                        contentAlignment = Alignment.Center,
                    ) {
                        val animationSpec = spring<Float>(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow,
                        )
                        AnimatedVisibility(
                            visible = searchBarState.targetValue == SearchBarValue.Expanded,
                            enter = scaleIn(animationSpec) + fadeIn(animationSpec),
                            exit = scaleOut(animationSpec) + fadeOut(animationSpec),
                        ) {
                            DPIconButton(
                                icon = DPIcons.Close,
                                variant = DPIconButtonVariant.Secondary,
                                contentDescription = "",
                                onClick = {
                                    scope.launch {
                                        searchBarState.animateToCollapsed()
                                    }
                                },
                            )
                        }
                        AnimatedVisibility(
                            visible = searchBarState.targetValue != SearchBarValue.Expanded,
                            enter = scaleIn(animationSpec) + fadeIn(animationSpec),
                            exit = scaleOut(animationSpec) + fadeOut(animationSpec),
                        ) {
                            DPIconButton(
                                icon = DPIcons.Search,
                                variant = DPIconButtonVariant.Secondary,
                                contentDescription = "",
                                onClick = {
                                    scope.launch {
                                        searchBarState.animateToExpanded()
                                    }
                                },
                            )
                        }
                    }
                },
            )
        }
    Screen(
        viewModel = viewModel,
        topAppBar = {
            AppBarWithSearch(
                state = searchBarState,
                contentPadding = PaddingValues(start = LocalDPSpacing.current.md),
                colors = appBarWithSearchColors,
                inputField = inputField,
                navigationIcon = {
                    Icon(
                        imageVector = DPIcons.devPulseIconLarge(),
                        contentDescription = "DevPulse",
                        tint = Color.Unspecified,
                    )
                },
                actions = {
                    DPIconButton(
                        icon = DPIcons.UserSettings,
                        variant = DPIconButtonVariant.Secondary,
                        contentDescription = "",
                        onClick = { navigator.navigate(Screen.Settings, onRootStack = true) },
                    )
                },
            )
        },
        onEffect = {},
    ) {
//        HomeScreenContent(state = this)

        ExpandedFullScreenContainedSearchBar(
            state = searchBarState,
            inputField = inputField,
            colors = appBarWithSearchColors.searchBarColors,
        ) {
            LazyColumn {
                items(10) {
                    DPClickableRow(
                        onClick = { },
                        content = {
                            DPTextView(
                                text = "Search Item $it",
                                variant = DPTextViewVariant.BodyMedium,
                            )
                        },
                    )
                }
            }
        }
    }
}

private data class TextResourceEntry(val label: String, val resource: TextResource)

@Composable
fun HomeScreenContent(
    state: HomeScreenState,
) {
    val entries = remember {
        listOf(
            TextResourceEntry(
                label = "Simple",
                resource = textResource { +"Hello, World!" },
            ),
            TextResourceEntry(
                label = "String resource",
                resource = textResource { stringRes(Res.string.text_resource_greeting) },
            ),
            TextResourceEntry(
                label = "String resource with args",
                resource = textResource {
                    stringRes(
                        Res.string.text_resource_greeting_with_name,
                        "Claude",
                    )
                },
            ),
            TextResourceEntry(
                label = "Plural resource (1 item)",
                resource = textResource {
                    pluralStringRes(
                        Res.plurals.text_resource_item_count,
                        1,
                    )
                },
            ),
            TextResourceEntry(
                label = "Plural resource (5 items)",
                resource = textResource {
                    pluralStringRes(
                        Res.plurals.text_resource_item_count,
                        5,
                    )
                },
            ),
            TextResourceEntry(
                label = "Joined with separator",
                resource = textResource(separator = " ") {
                    +"Three"
                    +"joined"
                    +"words"
                },
            ),
            TextResourceEntry(
                label = "Bold",
                resource = textResource { bold { +"Bold text" } },
            ),
            TextResourceEntry(
                label = "Italic",
                resource = textResource { italic { +"Italic text" } },
            ),
            TextResourceEntry(
                label = "Bold + italic nested",
                resource = textResource { bold { italic { +"Bold italic" } } },
            ),
            TextResourceEntry(
                label = "Mixed styles joined",
                resource = textResource(separator = " ") {
                    bold { +"Bold" }
                    +"regular"
                    italic { +"italic" }
                },
            ),
            TextResourceEntry(
                label = "URL link",
                resource = textResource {
                    url("https://kotlinlang.org") { +"Kotlin" }
                },
            ),
            TextResourceEntry(
                label = "Clickable",
                resource = textResource {
                    clickable(tag = "tap_me") { +"Tap me" }
                },
            ),
            TextResourceEntry(
                label = "Complex: plain + bold URL + plain",
                resource = textResource(separator = " ") {
                    +"Visit"
                    url("https://kotlinlang.org") { bold { +"Kotlin" } }
                    +"for docs"
                },
            ),
        )
    }

    LazyColumn(
        contentPadding = PaddingValues(
            horizontal = DPTheme.spacing.screenHorizontal,
            vertical = DPTheme.spacing.lg,
        ),
        verticalArrangement = Arrangement.spacedBy(DPTheme.spacing.md),
    ) {
        items(entries) { entry ->
            TextResourceExampleItem(entry = entry)
        }
    }
}

@Composable
private fun TextResourceExampleItem(entry: TextResourceEntry) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = DPTheme.spacing.xs),
        verticalArrangement = Arrangement.spacedBy(DPTheme.spacing.xs),
    ) {
        DPTextView(
            text = entry.label,
            variant = DPTextViewVariant.LabelSmall,
            color = MaterialTheme.colorScheme.secondary,
        )
        DPTextView(
            text = entry.resource.asAnnotatedString(),
            variant = DPTextViewVariant.BodyMedium,
        )
        DPHorizontalDivider(modifier = Modifier.padding(top = DPTheme.spacing.xs))
    }
}
