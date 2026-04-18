package dev.shounakmulay.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.BottomAppBarScrollBehavior
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LeadingIconTab
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemColors
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailDefaults
import androidx.compose.material3.NavigationRailItemColors
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabIndicatorScope
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TwoRowsTopAppBar
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailColors
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.WideNavigationRailItemDefaults
import androidx.compose.material3.WideNavigationRailState
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.NavigationItemColors
import androidx.compose.material3.NavigationItemIconPosition
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.ScrollState
import androidx.compose.material3.NavigationBarItem
import dev.shounakmulay.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.core.designsystem.compose.Preview

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DPTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    expandedHeight: Dp = TopAppBarDefaults.TopAppBarExpandedHeight,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    contentPadding: PaddingValues = TopAppBarDefaults.ContentPadding,
) = TopAppBar(
    title = title,
    modifier = modifier,
    navigationIcon = navigationIcon,
    actions = actions,
    expandedHeight = expandedHeight,
    windowInsets = windowInsets,
    colors = colors,
    scrollBehavior = scrollBehavior,
    contentPadding = contentPadding,
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DPTopAppBar(
    title: @Composable () -> Unit,
    subtitle: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    titleHorizontalAlignment: Alignment.Horizontal = Alignment.Start,
    expandedHeight: Dp = TopAppBarDefaults.TopAppBarExpandedHeight,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    contentPadding: PaddingValues = TopAppBarDefaults.ContentPadding,
) = TopAppBar(
    title = title,
    subtitle = subtitle,
    modifier = modifier,
    navigationIcon = navigationIcon,
    actions = actions,
    titleHorizontalAlignment = titleHorizontalAlignment,
    expandedHeight = expandedHeight,
    windowInsets = windowInsets,
    colors = colors,
    scrollBehavior = scrollBehavior,
    contentPadding = contentPadding,
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DPCenterAlignedTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    expandedHeight: Dp = TopAppBarDefaults.TopAppBarExpandedHeight,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    contentPadding: PaddingValues = TopAppBarDefaults.ContentPadding,
) = CenterAlignedTopAppBar(
    title = title,
    modifier = modifier,
    navigationIcon = navigationIcon,
    actions = actions,
    expandedHeight = expandedHeight,
    windowInsets = windowInsets,
    colors = colors,
    scrollBehavior = scrollBehavior,
    contentPadding = contentPadding,
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DPMediumTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    collapsedHeight: Dp = TopAppBarDefaults.MediumAppBarCollapsedHeight,
    expandedHeight: Dp = TopAppBarDefaults.MediumAppBarExpandedHeight,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
) = MediumTopAppBar(
    title = title,
    modifier = modifier,
    navigationIcon = navigationIcon,
    actions = actions,
    collapsedHeight = collapsedHeight,
    expandedHeight = expandedHeight,
    windowInsets = windowInsets,
    colors = colors,
    scrollBehavior = scrollBehavior,
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DPLargeTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    collapsedHeight: Dp = TopAppBarDefaults.LargeAppBarCollapsedHeight,
    expandedHeight: Dp = TopAppBarDefaults.LargeAppBarExpandedHeight,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
) = LargeTopAppBar(
    title = title,
    modifier = modifier,
    navigationIcon = navigationIcon,
    actions = actions,
    collapsedHeight = collapsedHeight,
    expandedHeight = expandedHeight,
    windowInsets = windowInsets,
    colors = colors,
    scrollBehavior = scrollBehavior,
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DPTwoRowsTopAppBar(
    title: @Composable (expanded: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: (@Composable (expanded: Boolean) -> Unit)? = null,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    titleHorizontalAlignment: Alignment.Horizontal = Alignment.Start,
    collapsedHeight: Dp = Dp.Unspecified,
    expandedHeight: Dp = Dp.Unspecified,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
) = TwoRowsTopAppBar(
    title = title,
    modifier = modifier,
    subtitle = subtitle,
    navigationIcon = navigationIcon,
    actions = actions,
    titleHorizontalAlignment = titleHorizontalAlignment,
    collapsedHeight = collapsedHeight,
    expandedHeight = expandedHeight,
    windowInsets = windowInsets,
    colors = colors,
    scrollBehavior = scrollBehavior,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DPBottomAppBar(
    actions: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    floatingActionButton: @Composable (() -> Unit)? = null,
    containerColor: Color = BottomAppBarDefaults.containerColor,
    contentColor: Color = contentColorFor(containerColor),
    tonalElevation: Dp = BottomAppBarDefaults.ContainerElevation,
    contentPadding: PaddingValues = BottomAppBarDefaults.ContentPadding,
    windowInsets: WindowInsets = BottomAppBarDefaults.windowInsets,
) = BottomAppBar(
    actions = actions,
    modifier = modifier,
    floatingActionButton = floatingActionButton,
    containerColor = containerColor,
    contentColor = contentColor,
    tonalElevation = tonalElevation,
    contentPadding = contentPadding,
    windowInsets = windowInsets,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DPBottomAppBar(
    actions: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    floatingActionButton: @Composable (() -> Unit)? = null,
    containerColor: Color = BottomAppBarDefaults.containerColor,
    contentColor: Color = contentColorFor(containerColor),
    tonalElevation: Dp = BottomAppBarDefaults.ContainerElevation,
    contentPadding: PaddingValues = BottomAppBarDefaults.ContentPadding,
    windowInsets: WindowInsets = BottomAppBarDefaults.windowInsets,
    scrollBehavior: BottomAppBarScrollBehavior? = null,
) = BottomAppBar(
    actions = actions,
    modifier = modifier,
    floatingActionButton = floatingActionButton,
    containerColor = containerColor,
    contentColor = contentColor,
    tonalElevation = tonalElevation,
    contentPadding = contentPadding,
    windowInsets = windowInsets,
    scrollBehavior = scrollBehavior,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DPBottomAppBar(
    modifier: Modifier = Modifier,
    containerColor: Color = BottomAppBarDefaults.containerColor,
    contentColor: Color = contentColorFor(containerColor),
    tonalElevation: Dp = BottomAppBarDefaults.ContainerElevation,
    contentPadding: PaddingValues = BottomAppBarDefaults.ContentPadding,
    windowInsets: WindowInsets = BottomAppBarDefaults.windowInsets,
    content: @Composable RowScope.() -> Unit,
) = BottomAppBar(
    modifier = modifier,
    containerColor = containerColor,
    contentColor = contentColor,
    tonalElevation = tonalElevation,
    contentPadding = contentPadding,
    windowInsets = windowInsets,
    content = content,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DPBottomAppBar(
    modifier: Modifier = Modifier,
    containerColor: Color = BottomAppBarDefaults.containerColor,
    contentColor: Color = contentColorFor(containerColor),
    tonalElevation: Dp = BottomAppBarDefaults.ContainerElevation,
    contentPadding: PaddingValues = BottomAppBarDefaults.ContentPadding,
    windowInsets: WindowInsets = BottomAppBarDefaults.windowInsets,
    scrollBehavior: BottomAppBarScrollBehavior? = null,
    content: @Composable RowScope.() -> Unit,
) = BottomAppBar(
    modifier = modifier,
    containerColor = containerColor,
    contentColor = contentColor,
    tonalElevation = tonalElevation,
    contentPadding = contentPadding,
    windowInsets = windowInsets,
    scrollBehavior = scrollBehavior,
    content = content,
)

@Composable
fun DPNavigationBar(
    modifier: Modifier = Modifier,
    containerColor: Color = NavigationBarDefaults.containerColor,
    contentColor: Color = MaterialTheme.colorScheme.contentColorFor(containerColor),
    tonalElevation: Dp = NavigationBarDefaults.Elevation,
    windowInsets: WindowInsets = NavigationBarDefaults.windowInsets,
    content: @Composable RowScope.() -> Unit,
) = NavigationBar(
    modifier = modifier,
    containerColor = containerColor,
    contentColor = contentColor,
    tonalElevation = tonalElevation,
    windowInsets = windowInsets,
    content = content,
)

@Composable
fun RowScope.DPNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    alwaysShowLabel: Boolean = true,
    colors: NavigationBarItemColors = NavigationBarItemDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = icon,
        modifier = modifier,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
        colors = colors,
        interactionSource = interactionSource,
    )
}

@Composable
fun DPNavigationRail(
    modifier: Modifier = Modifier,
    containerColor: Color = NavigationRailDefaults.ContainerColor,
    contentColor: Color = contentColorFor(containerColor),
    header: @Composable (ColumnScope.() -> Unit)? = null,
    windowInsets: WindowInsets = NavigationRailDefaults.windowInsets,
    content: @Composable ColumnScope.() -> Unit,
) = NavigationRail(
    modifier = modifier,
    containerColor = containerColor,
    contentColor = contentColor,
    header = header,
    windowInsets = windowInsets,
    content = content,
)

@Composable
fun ColumnScope.DPNavigationRailItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    alwaysShowLabel: Boolean = true,
    colors: NavigationRailItemColors = NavigationRailItemDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    androidx.compose.material3.NavigationRailItem(
        selected = selected,
        onClick = onClick,
        icon = icon,
        modifier = modifier,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
        colors = colors,
        interactionSource = interactionSource,
    )
}

@Composable
fun DPWideNavigationRail(
    modifier: Modifier = Modifier,
    state: WideNavigationRailState = rememberWideNavigationRailState(),
    shape: Shape = WideNavigationRailDefaults.shape,
    colors: WideNavigationRailColors = WideNavigationRailDefaults.colors(),
    header: @Composable (() -> Unit)? = null,
    windowInsets: WindowInsets = WideNavigationRailDefaults.windowInsets,
    arrangement: Arrangement.Vertical = WideNavigationRailDefaults.arrangement,
    contentPadding: PaddingValues = WideNavigationRailDefaults.ContentPadding,
    content: @Composable () -> Unit,
) = WideNavigationRail(
    modifier = modifier,
    state = state,
    shape = shape,
    colors = colors,
    header = header,
    windowInsets = windowInsets,
    arrangement = arrangement,
    contentPadding = contentPadding,
    content = content,
)

@Composable
fun DPWideNavigationRailItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    label: @Composable (() -> Unit)?,
    railExpanded: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    iconPosition: NavigationItemIconPosition =
        WideNavigationRailItemDefaults.iconPositionFor(railExpanded),
    colors: NavigationItemColors = WideNavigationRailItemDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
    indicatorPadding: PaddingValues =
        WideNavigationRailItemDefaults.indicatorPadding(railExpanded = railExpanded),
) = WideNavigationRailItem(
    selected = selected,
    onClick = onClick,
    icon = icon,
    label = label,
    railExpanded = railExpanded,
    modifier = modifier,
    enabled = enabled,
    iconPosition = iconPosition,
    colors = colors,
    interactionSource = interactionSource,
    indicatorPadding = indicatorPadding,
)

@Composable
fun DPModalNavigationDrawer(
    drawerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    gesturesEnabled: Boolean = true,
    scrimColor: Color = DrawerDefaults.scrimColor,
    content: @Composable () -> Unit,
) = ModalNavigationDrawer(
    drawerContent = drawerContent,
    modifier = modifier,
    drawerState = drawerState,
    gesturesEnabled = gesturesEnabled,
    scrimColor = scrimColor,
    content = content,
)

@Composable
fun DPDismissibleNavigationDrawer(
    drawerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    gesturesEnabled: Boolean = true,
    content: @Composable () -> Unit,
) = DismissibleNavigationDrawer(
    drawerContent = drawerContent,
    modifier = modifier,
    drawerState = drawerState,
    gesturesEnabled = gesturesEnabled,
    content = content,
)

@Composable
fun DPPermanentNavigationDrawer(
    drawerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) = PermanentNavigationDrawer(
    drawerContent = drawerContent,
    modifier = modifier,
    content = content,
)

@Composable
fun DPNavigationDrawerItem(
    label: @Composable () -> Unit,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    badge: (@Composable () -> Unit)? = null,
    colors: NavigationDrawerItemColors = NavigationDrawerItemDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
) = NavigationDrawerItem(
    label = label,
    selected = selected,
    onClick = onClick,
    modifier = modifier,
    icon = icon,
    badge = badge,
    colors = colors,
    interactionSource = interactionSource,
)

@Composable
fun DPModalDrawerSheet(
    modifier: Modifier = Modifier,
    drawerShape: Shape = DrawerDefaults.shape,
    drawerContainerColor: Color = DrawerDefaults.modalContainerColor,
    drawerContentColor: Color = contentColorFor(drawerContainerColor),
    drawerTonalElevation: Dp = DrawerDefaults.ModalDrawerElevation,
    windowInsets: WindowInsets = DrawerDefaults.windowInsets,
    content: @Composable ColumnScope.() -> Unit,
) = ModalDrawerSheet(
    modifier = modifier,
    drawerShape = drawerShape,
    drawerContainerColor = drawerContainerColor,
    drawerContentColor = drawerContentColor,
    drawerTonalElevation = drawerTonalElevation,
    windowInsets = windowInsets,
    content = content,
)

@Composable
fun DPModalDrawerSheet(
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    drawerShape: Shape = DrawerDefaults.shape,
    drawerContainerColor: Color = DrawerDefaults.modalContainerColor,
    drawerContentColor: Color = contentColorFor(drawerContainerColor),
    drawerTonalElevation: Dp = DrawerDefaults.ModalDrawerElevation,
    windowInsets: WindowInsets = DrawerDefaults.windowInsets,
    content: @Composable ColumnScope.() -> Unit,
) = ModalDrawerSheet(
    drawerState = drawerState,
    modifier = modifier,
    drawerShape = drawerShape,
    drawerContainerColor = drawerContainerColor,
    drawerContentColor = drawerContentColor,
    drawerTonalElevation = drawerTonalElevation,
    windowInsets = windowInsets,
    content = content,
)

@Composable
fun DPDismissibleDrawerSheet(
    modifier: Modifier = Modifier,
    drawerShape: Shape = RectangleShape,
    drawerContainerColor: Color = DrawerDefaults.standardContainerColor,
    drawerContentColor: Color = contentColorFor(drawerContainerColor),
    drawerTonalElevation: Dp = DrawerDefaults.DismissibleDrawerElevation,
    windowInsets: WindowInsets = DrawerDefaults.windowInsets,
    content: @Composable ColumnScope.() -> Unit,
) = DismissibleDrawerSheet(
    modifier = modifier,
    drawerShape = drawerShape,
    drawerContainerColor = drawerContainerColor,
    drawerContentColor = drawerContentColor,
    drawerTonalElevation = drawerTonalElevation,
    windowInsets = windowInsets,
    content = content,
)

@Composable
fun DPDismissibleDrawerSheet(
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    drawerShape: Shape = RectangleShape,
    drawerContainerColor: Color = DrawerDefaults.standardContainerColor,
    drawerContentColor: Color = contentColorFor(drawerContainerColor),
    drawerTonalElevation: Dp = DrawerDefaults.DismissibleDrawerElevation,
    windowInsets: WindowInsets = DrawerDefaults.windowInsets,
    content: @Composable ColumnScope.() -> Unit,
) = DismissibleDrawerSheet(
    drawerState = drawerState,
    modifier = modifier,
    drawerShape = drawerShape,
    drawerContainerColor = drawerContainerColor,
    drawerContentColor = drawerContentColor,
    drawerTonalElevation = drawerTonalElevation,
    windowInsets = windowInsets,
    content = content,
)

@Composable
fun DPPermanentDrawerSheet(
    modifier: Modifier = Modifier,
    drawerShape: Shape = RectangleShape,
    drawerContainerColor: Color = DrawerDefaults.standardContainerColor,
    drawerContentColor: Color = contentColorFor(drawerContainerColor),
    drawerTonalElevation: Dp = DrawerDefaults.PermanentDrawerElevation,
    windowInsets: WindowInsets = DrawerDefaults.windowInsets,
    content: @Composable ColumnScope.() -> Unit,
) = PermanentDrawerSheet(
    modifier = modifier,
    drawerShape = drawerShape,
    drawerContainerColor = drawerContainerColor,
    drawerContentColor = drawerContentColor,
    drawerTonalElevation = drawerTonalElevation,
    windowInsets = windowInsets,
    content = content,
)

@Composable
fun DPPrimaryTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    containerColor: Color = TabRowDefaults.primaryContainerColor,
    contentColor: Color = TabRowDefaults.primaryContentColor,
    indicator: @Composable TabIndicatorScope.() -> Unit = {
        TabRowDefaults.PrimaryIndicator(
            modifier = Modifier.tabIndicatorOffset(selectedTabIndex, matchContentSize = true),
            width = Dp.Unspecified,
        )
    },
    divider: @Composable () -> Unit = @Composable { HorizontalDivider() },
    tabs: @Composable () -> Unit,
) = PrimaryTabRow(
    selectedTabIndex = selectedTabIndex,
    modifier = modifier,
    containerColor = containerColor,
    contentColor = contentColor,
    indicator = indicator,
    divider = divider,
    tabs = tabs,
)

@Composable
fun DPSecondaryTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    containerColor: Color = TabRowDefaults.secondaryContainerColor,
    contentColor: Color = TabRowDefaults.secondaryContentColor,
    indicator: @Composable TabIndicatorScope.() -> Unit =
        @Composable {
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(selectedTabIndex, matchContentSize = false),
            )
        },
    divider: @Composable () -> Unit = @Composable { HorizontalDivider() },
    tabs: @Composable () -> Unit,
) = SecondaryTabRow(
    selectedTabIndex = selectedTabIndex,
    modifier = modifier,
    containerColor = containerColor,
    contentColor = contentColor,
    indicator = indicator,
    divider = divider,
    tabs = tabs,
)

@Composable
fun DPPrimaryScrollableTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    containerColor: Color = TabRowDefaults.primaryContainerColor,
    contentColor: Color = TabRowDefaults.primaryContentColor,
    edgePadding: Dp = TabRowDefaults.ScrollableTabRowEdgeStartPadding,
    indicator: @Composable TabIndicatorScope.() -> Unit =
        @Composable {
            TabRowDefaults.PrimaryIndicator(
                Modifier.tabIndicatorOffset(selectedTabIndex, matchContentSize = true),
                width = Dp.Unspecified,
            )
        },
    divider: @Composable () -> Unit = @Composable { HorizontalDivider() },
    minTabWidth: Dp = TabRowDefaults.ScrollableTabRowMinTabWidth,
    tabs: @Composable () -> Unit,
) = PrimaryScrollableTabRow(
    selectedTabIndex = selectedTabIndex,
    modifier = modifier,
    scrollState = scrollState,
    containerColor = containerColor,
    contentColor = contentColor,
    edgePadding = edgePadding,
    indicator = indicator,
    divider = divider,
    minTabWidth = minTabWidth,
    tabs = tabs,
)

@Composable
fun DPSecondaryScrollableTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    containerColor: Color = TabRowDefaults.secondaryContainerColor,
    contentColor: Color = TabRowDefaults.secondaryContentColor,
    edgePadding: Dp = TabRowDefaults.ScrollableTabRowEdgeStartPadding,
    indicator: @Composable TabIndicatorScope.() -> Unit =
        @Composable {
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(selectedTabIndex, matchContentSize = false),
            )
        },
    divider: @Composable () -> Unit = @Composable { HorizontalDivider() },
    minTabWidth: Dp = TabRowDefaults.ScrollableTabRowMinTabWidth,
    tabs: @Composable () -> Unit,
) = SecondaryScrollableTabRow(
    selectedTabIndex = selectedTabIndex,
    modifier = modifier,
    scrollState = scrollState,
    containerColor = containerColor,
    contentColor = contentColor,
    edgePadding = edgePadding,
    indicator = indicator,
    divider = divider,
    minTabWidth = minTabWidth,
    tabs = tabs,
)

@Composable
fun DPTab(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    selectedContentColor: Color = LocalContentColor.current,
    unselectedContentColor: Color = selectedContentColor,
    interactionSource: MutableInteractionSource? = null,
) = Tab(
    selected = selected,
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    text = text,
    icon = icon,
    selectedContentColor = selectedContentColor,
    unselectedContentColor = unselectedContentColor,
    interactionSource = interactionSource,
)

@Composable
fun DPTab(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selectedContentColor: Color = LocalContentColor.current,
    unselectedContentColor: Color = selectedContentColor,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable ColumnScope.() -> Unit,
) = Tab(
    selected = selected,
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    selectedContentColor = selectedContentColor,
    unselectedContentColor = unselectedContentColor,
    interactionSource = interactionSource,
    content = content,
)

@Composable
fun DPLeadingIconTab(
    selected: Boolean,
    onClick: () -> Unit,
    text: @Composable () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selectedContentColor: Color = LocalContentColor.current,
    unselectedContentColor: Color = selectedContentColor,
    interactionSource: MutableInteractionSource? = null,
) = LeadingIconTab(
    selected = selected,
    onClick = onClick,
    text = text,
    icon = icon,
    modifier = modifier,
    enabled = enabled,
    selectedContentColor = selectedContentColor,
    unselectedContentColor = unselectedContentColor,
    interactionSource = interactionSource,
)

@OptIn(ExperimentalMaterial3Api::class)
@DPComponentPreview
@Composable
private fun DPNavigationPreview() {
    Preview {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            DPTopAppBar(title = { Text("Title") })
            DPNavigationBar {
                DPNavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                    label = { Text("Home") },
                )
                DPNavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                    label = { Text("More") },
                )
            }
        }
    }
}
