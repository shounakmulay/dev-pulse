package dev.shounakmulay.core.designsystem.components.devtools

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.components.DPModalBottomSheet
import dev.shounakmulay.core.designsystem.theme.DefaultIconSize
import dev.shounakmulay.core.designsystem.theme.DefaultSpacing
import dev.shounakmulay.core.designsystem.theme.LocalDPIconSize
import dev.shounakmulay.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.core.designsystem.theme.appTypography
import dev.shounakmulay.core.designsystem.theme.calmMotionScheme
import dev.shounakmulay.core.designsystem.theme.darkScheme
import dev.shounakmulay.core.designsystem.theme.dpShapes
import dev.shounakmulay.core.designsystem.theme.getColorScheme
import dev.shounakmulay.core.designsystem.theme.lightScheme
import dev.shounakmulay.core.navigation.Navigator

enum class BoardSection(val title: String) {
    Colors("Colors"),
    SemanticColors("Semantic Colors"),
    Typography("Typography"),
    Shapes("Shapes"),
    Elevation("Elevation"),
    Motion("Motion"),
    Spacing("Spacing"),
    IconSize("Icon Size"),
    Buttons("Buttons"),
    IconButtonsAndFabs("Icon Buttons & FABs"),
    CardsAndLists("Cards & Lists"),
    Chips("Chips"),
    TextFields("Text Fields"),
    SelectionControls("Selection Controls"),
    SegmentedAndToolbars("Segmented & Toolbars"),
    Navigation("Navigation"),
    Feedback("Feedback"),
    DialogsAndSheets("Dialogs & Sheets"),
    MenusAndPickers("Menus & Pickers"),
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DesignSystemBoard(navigator: Navigator) {
    var selected by remember { mutableStateOf<BoardSection?>(null) }
    var themeMode by remember { mutableStateOf<ThemeMode>(ThemeMode.Light) }
    var showThemePicker by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val colorScheme = themeMode.resolveColorScheme()

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        typography = appTypography(),
        shapes = dpShapes,
        motionScheme = calmMotionScheme(),
    ) {
        CompositionLocalProvider(
            LocalDPSpacing provides DefaultSpacing,
            LocalDPIconSize provides DefaultIconSize,
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(selected?.title ?: "Design System") },
                        navigationIcon = {
                            IconButton(onClick = {
                                if (selected != null) {
                                    selected = null
                                } else {
                                    navigator.navigateBack()
                                }
                            }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { showThemePicker = true }) {
                                Icon(Icons.Default.Palette, contentDescription = "Switch theme")
                            }
                        },
                    )
                },
            ) { padding ->
                AnimatedContent(
                    targetState = selected,
                    modifier = Modifier.padding(padding),
                ) { section ->
                    when (section) {
                        null -> SectionList(onSectionClick = { selected = it })
                        BoardSection.Colors -> ColorBoard(Modifier.fillMaxSize())
                        BoardSection.SemanticColors -> SemanticColorsBoard(Modifier.fillMaxSize())
                        BoardSection.Typography -> TypographyBoard(Modifier.fillMaxSize())
                        BoardSection.Shapes -> ShapeBoard(Modifier.fillMaxSize())
                        BoardSection.Elevation -> ElevationBoard(Modifier.fillMaxSize())
                        BoardSection.Motion -> MotionBoard(Modifier.fillMaxSize())
                        BoardSection.Spacing -> SpacingBoard(Modifier.fillMaxSize())
                        BoardSection.IconSize -> IconSizeBoard(Modifier.fillMaxSize())
                        BoardSection.Buttons -> ButtonsBoard(Modifier.fillMaxSize())
                        BoardSection.IconButtonsAndFabs -> IconButtonsAndFabsBoard(Modifier.fillMaxSize())
                        BoardSection.CardsAndLists -> CardsAndListsBoard(Modifier.fillMaxSize())
                        BoardSection.Chips -> ChipsBoard(Modifier.fillMaxSize())
                        BoardSection.TextFields -> TextFieldsBoard(Modifier.fillMaxSize())
                        BoardSection.SelectionControls -> SelectionControlsBoard(Modifier.fillMaxSize())
                        BoardSection.SegmentedAndToolbars -> SegmentedAndToolbarsBoard(Modifier.fillMaxSize())
                        BoardSection.Navigation -> NavigationBoard(Modifier.fillMaxSize())
                        BoardSection.Feedback -> FeedbackBoard(Modifier.fillMaxSize())
                        BoardSection.DialogsAndSheets -> DialogsAndSheetsBoard(Modifier.fillMaxSize())
                        BoardSection.MenusAndPickers -> MenusAndPickersBoard(Modifier.fillMaxSize())
                    }
                }
            }

            if (showThemePicker) {
                DPModalBottomSheet(
                    onDismissRequest = { showThemePicker = false },
                    sheetState = sheetState,
                ) {
                    ThemeSwitcherSheetContent(
                        selected = themeMode,
                        onSelect = { themeMode = it },
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeMode.resolveColorScheme(): ColorScheme = when (this) {
    ThemeMode.DynamicLight -> getColorScheme(dynamicColor = true, darkTheme = false) ?: lightScheme
    ThemeMode.DynamicDark -> getColorScheme(dynamicColor = true, darkTheme = true) ?: darkScheme
    else -> toColorScheme()
}

@Composable
private fun SectionList(
    onSectionClick: (BoardSection) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        items(BoardSection.entries.toList()) { section ->
            ListItem(
                headlineContent = { Text(section.title) },
                modifier = Modifier.clickable { onSectionClick(section) },
            )
            HorizontalDivider()
        }
    }
}
