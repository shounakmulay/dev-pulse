package dev.shounakmulay.devpulse.feature.settings.screens.settings.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonGroup
import dev.shounakmulay.devpulse.core.designsystem.components.DPListItem
import dev.shounakmulay.devpulse.core.designsystem.components.DPSegmentedButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPSingleChoiceSegmentedButtonRow
import dev.shounakmulay.devpulse.core.designsystem.components.DPSwitch
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPTopAppBar
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.DPTheme
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.domain.models.post.FeedsPostListItemVariant
import dev.shounakmulay.devpulse.core.domain.models.theme.ThemeMode
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.button.DPBackNavigationIconButton
import dev.shounakmulay.devpulse.core.ui.list.post.FeedPostListItem
import dev.shounakmulay.devpulse.core.ui.screen.Screen
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import devpulse.core.resources.generated.resources.appearance
import devpulse.core.resources.generated.resources.article_reader
import devpulse.core.resources.generated.resources.black_mode
import devpulse.core.resources.generated.resources.design_system_board
import devpulse.core.resources.generated.resources.developer_tools
import devpulse.core.resources.generated.resources.licenses
import devpulse.core.resources.generated.resources.others
import devpulse.core.resources.generated.resources.settings
import devpulse.core.resources.generated.resources.theme
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, navigator: Navigator) {
    Screen(
        viewModel = viewModel,
        onEffect = {
            when (it) {
                SettingsScreenEffect.NavigateToDesignSystemBoard -> navigator.navigate(Screen.DeveloperTools.DesignSystemBoard)
                SettingsScreenEffect.NavigateToLicenses -> navigator.navigate(Screen.AboutLibs)
                SettingsScreenEffect.NavigateToArticleSettings -> navigator.navigate(Screen.Settings.ArticleSettings)
                else -> viewModel.unhandledEffect(it)
            }
        },
        topAppBar = {
            DPTopAppBar(
                title = stringResource(stringRes.settings),
                navigationIcon = {
                    DPBackNavigationIconButton(onNavigateBack = navigator::navigateBack)
                }
            )
        },
    ) {
        val canToggleBlackMode = it.canToggleBlackMode(isDarkTheme = DPTheme.isDarkTheme)

        LazyColumn {
            item {
                ThemeSettingsSection(
                    selectedThemeMode = it.themeMode,
                    isBlackMode = it.isBlackMode,
                    canToggleBlackMode = canToggleBlackMode,
                    onThemeModeSelected = { value ->
                        viewModel.onEvent(SettingsScreenEvent.OnThemeModeSelected(value))
                    },
                    onBlackModeToggled = { value ->
                        viewModel.onEvent(SettingsScreenEvent.OnBlackModeToggled(value))
                    }
                )
            }
            item {
                AppearanceSettingsSection(
                    selectedVariant = it.feedPostListItemVariant,
                    onVariantSelected = { variant ->
                        viewModel.onEvent(
                            SettingsScreenEvent.OnFeedPostListItemVariantSelected(
                                variant
                            )
                        )
                    },
                    onArticleSettingsClick = {
                        viewModel.onEvent(SettingsScreenEvent.OnArticleSettingsClicked)
                    }
                )
            }
            item {
                OthersSection(
                    onLicensesClick = {
                        viewModel.onEvent(SettingsScreenEvent.OnLicensesClicked)
                    }
                )
            }
            item {
                DeveloperSettingsSection(
                    onDesignSystemBoardClick = {
                        viewModel.onEvent(SettingsScreenEvent.OnDesignSystemBoardClicked)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppearanceSettingsSection(
    selectedVariant: FeedsPostListItemVariant,
    onVariantSelected: (FeedsPostListItemVariant) -> Unit,
    onArticleSettingsClick: () -> Unit
) {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(LocalDPSpacing.current.sm)
    ) {
        SettingsSectionHeading(stringResource(stringRes.appearance))
        SettingsSubPageLink(
            headlineText = stringResource(stringRes.article_reader),
            onClick = onArticleSettingsClick
        )
        var bookmarked by remember { mutableStateOf(false) }
        DPButtonGroup(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LocalDPSpacing.current.md)
                .horizontalScroll(rememberScrollState()),
            overflowIndicator = {},
        ) {
            FeedsPostListItemVariant.entries.forEach { variant ->
                val isSelected = selectedVariant == variant

                toggleableItem(
                    checked = isSelected,
                    label = variant.name,
                    onCheckedChange = { checked ->
                        if (checked) {
                            onVariantSelected(variant)
                        }
                    }
                )
            }
        }
        FeedPostListItem(
            modifier = Modifier.padding(horizontal = LocalDPSpacing.current.md)
                .animateContentSize(),
            variant = selectedVariant,
            showImage = true,
            title = TextResource.fromText("As We May Think"),
            description = TextResource.fromText(
                "Vannevar Bush imagines the memex, a device for storing and linking books, records, " +
                        "and communications to help people explore knowledge and extend their memory."
            ),
            context = null,
            imageUrl = "https://cdn.theatlantic.com/thumbor/bZX1oW71zQgJwzuSKMaGQSW7_w8=/1x416:2999x2102/976x549/media/img/2018/03/AP_413517775098/original.jpg",
            websiteImageUrl = "https://cdn.theatlantic.com/_next/static/images/favicon-3888b0e329526a975703e3059a02b92d.ico",
            feedInitials = "TA",
            feedTitle = "The Atlantic",
            publishedText = "July 1945",
            createdAt = "July 1945",
            bookmarked = bookmarked,
            onBookmarkChanged = { bookmarked = it },
            onPostClick = {}
        )
    }
}

@Composable
private fun ThemeSettingsSection(
    selectedThemeMode: ThemeMode,
    isBlackMode: Boolean,
    canToggleBlackMode: Boolean,
    onThemeModeSelected: (ThemeMode) -> Unit,
    onBlackModeToggled: (Boolean) -> Unit
) {
    Column {
        SettingsSectionHeading(title = stringResource(stringRes.theme))
        ThemeModeSelector(
            selectedThemeMode = selectedThemeMode,
            onValueSelected = onThemeModeSelected
        )
        SettingsToggle(
            checked = isBlackMode,
            headlineText = stringResource(stringRes.black_mode),
            supportingText = null,
            enabled = canToggleBlackMode,
            onClick = onBlackModeToggled
        )
    }
}

@Composable
private fun DeveloperSettingsSection(
    onDesignSystemBoardClick: () -> Unit
) {
    Column {
        SettingsSectionHeading(title = stringResource(stringRes.developer_tools))
        SettingsSubPageLink(
            headlineText = stringResource(stringRes.design_system_board),
            onClick = onDesignSystemBoardClick
        )
    }
}

@Composable
private fun OthersSection(onLicensesClick: () -> Unit) {
    Column {
        SettingsSectionHeading(title = stringResource(stringRes.others))
        SettingsSubPageLink(
            headlineText = stringResource(stringRes.licenses),
            onClick = onLicensesClick
        )
    }
}

@Composable
private fun ThemeModeSelector(
    selectedThemeMode: ThemeMode,
    onValueSelected: (ThemeMode) -> Unit
) {
    val textMeasurer = rememberTextMeasurer()
    val textStyle = MaterialTheme.typography.labelMedium
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val iconSize = SegmentedButtonDefaults.IconSize
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        val showLabels = with(density) {
            val count = ThemeMode.entries.size
            val overlap = SegmentedButtonDefaults.BorderWidth.roundToPx()
            val width = (constraints.maxWidth + overlap * (count - 1)) / count
            val padding = SegmentedButtonDefaults.ContentPadding
            val contentInset = padding.calculateStartPadding(layoutDirection).roundToPx() +
                    padding.calculateEndPadding(layoutDirection).roundToPx() +
                    iconSize.roundToPx() + 8.dp.roundToPx()
            ThemeMode.entries.all { themeMode ->
                val textWidth = textMeasurer.measure(
                    text = themeMode.label(),
                    style = textStyle,
                    maxLines = 1,
                    softWrap = false,
                ).size.width
                textWidth <= width - contentInset
            }
        }
        DPSingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth(),
        ) {
            ThemeMode.entries.forEachIndexed { index, themeMode ->
                DPSegmentedButton(
                    selected = themeMode == selectedThemeMode,
                    onClick = { onValueSelected(themeMode) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = ThemeMode.entries.size
                    ),
                    icon = {
                        val icon = when (themeMode) {
                            ThemeMode.LIGHT -> DPIcons.LightTheme
                            ThemeMode.DARK -> DPIcons.DarkTheme
                            ThemeMode.SYSTEM -> DPIcons.SystemTheme
                        }
                        Icon(
                            imageVector = icon,
                            modifier = Modifier.size(iconSize),
                            contentDescription = themeMode.label()
                        )
                    },
                    label = {
                        DPTextView(
                            text = if (showLabels) themeMode.label() else "",
                            variant = DPTextViewVariant.LabelMedium,
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun SettingsToggle(
    checked: Boolean,
    headlineText: String,
    supportingText: String?,
    enabled: Boolean,
    onClick: (Boolean) -> Unit
) {
    DPListItem(
        headlineText = headlineText,
        supportingText = supportingText,
        enabled = enabled,
        onClick = {
            onClick(!checked)
        },
        trailingContent = {
            DPSwitch(
                checked = checked,
                onCheckedChange = {
                    onClick(it)
                },
                enabled = enabled
            )
        }
    )
}

private fun ThemeMode.label(): String =
    when (this) {
        ThemeMode.LIGHT -> "Light"
        ThemeMode.DARK -> "Dark"
        ThemeMode.SYSTEM -> "System"
    }

@Composable
private fun SettingsSectionHeading(title: String) {
    DPTextView(
        modifier = Modifier.padding(16.dp),
        text = title,
        variant = DPTextViewVariant.TitleMediumEmphasized
    )
}

@Composable
private fun SettingsSubPageLink(
    headlineText: String,
    onClick: () -> Unit
) {
    Column {
        DPListItem(
            headlineText = headlineText,
            onClick = onClick,
            trailingContent = {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "",
                )
            }
        )
    }
}
