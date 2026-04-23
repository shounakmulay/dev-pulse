@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package dev.shounakmulay.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LeadingIconTab
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailDefaults
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemColors
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.PermanentDrawerSheet
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
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import dev.shounakmulay.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.core.designsystem.compose.Preview
import dev.shounakmulay.core.designsystem.theme.DPElevationLevel
import dev.shounakmulay.core.designsystem.theme.DPTheme
import dev.shounakmulay.core.designsystem.theme.value

// -----------------------------------------------------------------------------
// Top app bar
// -----------------------------------------------------------------------------

enum class DPTopAppBarVariant {
    Small,
    CenterAligned,
    Medium,
    Large,
    TwoRows,
}


@Composable
fun DPTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    variant: DPTopAppBarVariant = DPTopAppBarVariant.Small,
    navigationIcon: ImageVector? = null,
    onNavigationClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    colors: TopAppBarColors? = null,
    windowInsets: WindowInsets? = null,
) {
    val nav: @Composable () -> Unit = {
        if (navigationIcon != null) {
            IconButton(onClick = onNavigationClick ?: {}) {
                Icon(
                    imageVector = navigationIcon,
                    contentDescription = null,
                )
            }
        }
    }

    DPTopAppBar(
        title = title,
        modifier = modifier,
        subtitle = subtitle,
        variant = variant,
        navigationIcon = nav,
        actions = actions,
        scrollBehavior = scrollBehavior,
        colors = colors,
        windowInsets = windowInsets
    )

}

@Composable
fun DPTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    variant: DPTopAppBarVariant = DPTopAppBarVariant.Small,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    colors: TopAppBarColors? = null,
    windowInsets: WindowInsets? = null,
) {
    val titleSlot: @Composable () -> Unit = { DPTextView(text = title, variant = DPTextViewVariant.TitleLarge) }
    val subtitleSlot: @Composable () -> Unit = {
        DPTextView(
            text = subtitle!!,
            variant = DPTextViewVariant.LabelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
    val resolvedColors = colors ?: TopAppBarDefaults.topAppBarColors()
    val resolvedInsets = windowInsets ?: TopAppBarDefaults.windowInsets

    when (variant) {
        DPTopAppBarVariant.Small -> {
            if (subtitle != null) {
                TopAppBar(
                    title = titleSlot,
                    subtitle = subtitleSlot,
                    modifier = modifier,
                    navigationIcon = navigationIcon,
                    actions = actions,
                    titleHorizontalAlignment = Alignment.Start,
                    expandedHeight = TopAppBarDefaults.TopAppBarExpandedHeight,
                    windowInsets = resolvedInsets,
                    colors = resolvedColors,
                    scrollBehavior = scrollBehavior,
                    contentPadding = TopAppBarDefaults.ContentPadding,
                )
            } else {
                TopAppBar(
                    title = titleSlot,
                    modifier = modifier,
                    navigationIcon = navigationIcon,
                    actions = actions,
                    expandedHeight = TopAppBarDefaults.TopAppBarExpandedHeight,
                    windowInsets = resolvedInsets,
                    colors = resolvedColors,
                    scrollBehavior = scrollBehavior,
                    contentPadding = TopAppBarDefaults.ContentPadding,
                )
            }
        }

        DPTopAppBarVariant.CenterAligned -> {
            val titleForCenter: @Composable () -> Unit = {
                if (subtitle != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        DPTextView(text = title, variant = DPTextViewVariant.TitleLarge)
                        DPTextView(
                            text = subtitle,
                            variant = DPTextViewVariant.LabelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    DPTextView(text = title, variant = DPTextViewVariant.TitleLarge)
                }
            }
            CenterAlignedTopAppBar(
                title = titleForCenter,
                modifier = modifier,
                navigationIcon = navigationIcon,
                actions = actions,
                expandedHeight = TopAppBarDefaults.TopAppBarExpandedHeight,
                windowInsets = resolvedInsets,
                colors = resolvedColors,
                scrollBehavior = scrollBehavior,
                contentPadding = TopAppBarDefaults.ContentPadding,
            )
        }

        DPTopAppBarVariant.Medium -> {
            val titleForMedium: @Composable () -> Unit = {
                if (subtitle != null) {
                    Column {
                        DPTextView(text = title, variant = DPTextViewVariant.TitleLarge)
                        DPTextView(
                            text = subtitle,
                            variant = DPTextViewVariant.LabelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    DPTextView(text = title, variant = DPTextViewVariant.TitleLarge)
                }
            }
            MediumTopAppBar(
                title = titleForMedium,
                modifier = modifier,
                navigationIcon = navigationIcon,
                actions = actions,
                collapsedHeight = TopAppBarDefaults.MediumAppBarCollapsedHeight,
                expandedHeight = TopAppBarDefaults.MediumAppBarExpandedHeight,
                windowInsets = resolvedInsets,
                colors = resolvedColors,
                scrollBehavior = scrollBehavior,
            )
        }

        DPTopAppBarVariant.Large -> {
            val titleForLarge: @Composable () -> Unit = {
                if (subtitle != null) {
                    Column {
                        DPTextView(text = title, variant = DPTextViewVariant.TitleLarge)
                        DPTextView(
                            text = subtitle,
                            variant = DPTextViewVariant.LabelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    DPTextView(text = title, variant = DPTextViewVariant.TitleLarge)
                }
            }
            LargeTopAppBar(
                title = titleForLarge,
                modifier = modifier,
                navigationIcon = navigationIcon,
                actions = actions,
                collapsedHeight = TopAppBarDefaults.LargeAppBarCollapsedHeight,
                expandedHeight = TopAppBarDefaults.LargeAppBarExpandedHeight,
                windowInsets = resolvedInsets,
                colors = resolvedColors,
                scrollBehavior = scrollBehavior,
            )
        }

        DPTopAppBarVariant.TwoRows -> {
            TwoRowsTopAppBar(
                title = { DPTextView(text = title, variant = DPTextViewVariant.TitleLarge) },
                modifier = modifier,
                subtitle = if (subtitle != null) {
                    {
                        DPTextView(
                            text = subtitle,
                            variant = DPTextViewVariant.LabelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    null
                },
                navigationIcon = navigationIcon,
                actions = actions,
                titleHorizontalAlignment = Alignment.Start,
                collapsedHeight = Dp.Unspecified,
                expandedHeight = Dp.Unspecified,
                windowInsets = resolvedInsets,
                colors = resolvedColors,
                scrollBehavior = scrollBehavior,
            )
        }
    }
}


// -----------------------------------------------------------------------------
// Navigation bar
// -----------------------------------------------------------------------------

@Composable
fun DPNavigationBar(
    modifier: Modifier = Modifier,
    containerColor: Color? = null,
    contentColor: Color? = null,
    tonalElevation: DPElevationLevel = DPElevationLevel.Level2,
    windowInsets: WindowInsets? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val bg = containerColor ?: NavigationBarDefaults.containerColor
    val c = contentColor ?: MaterialTheme.colorScheme.contentColorFor(bg)
    NavigationBar(
        modifier = modifier,
        containerColor = bg,
        contentColor = c,
        tonalElevation = tonalElevation.value(),
        windowInsets = windowInsets ?: NavigationBarDefaults.windowInsets,
        content = content,
    )
}

@Composable
fun RowScope.DPNavigationBarItem(
    text: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedIcon: ImageVector = icon,
    alwaysShowLabel: Boolean = true,
    badgeText: String? = null,
    enabled: Boolean = true,
    colors: NavigationBarItemColors? = null,
) {
    val resolvedColors = colors ?: NavigationBarItemDefaults.colors()
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            if (badgeText != null) {
                BadgedBox(
                    badge = {
                        Badge { Text(badgeText) }
                    },
                ) {
                    Icon(
                        imageVector = if (selected) selectedIcon else icon,
                        contentDescription = null,
                    )
                }
            } else {
                Icon(
                    imageVector = if (selected) selectedIcon else icon,
                    contentDescription = null,
                )
            }
        },
        label = { DPTextView(text = text, variant = DPTextViewVariant.LabelSmall) },
        modifier = modifier,
        enabled = enabled,
        alwaysShowLabel = alwaysShowLabel,
        colors = resolvedColors,
    )
}

// -----------------------------------------------------------------------------
// Navigation rail
// -----------------------------------------------------------------------------

@Composable
fun DPNavigationRail(
    modifier: Modifier = Modifier,
    header: @Composable (ColumnScope.() -> Unit)? = null,
    containerColor: Color? = null,
    contentColor: Color? = null,
    windowInsets: WindowInsets? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val bg = containerColor ?: NavigationRailDefaults.ContainerColor
    val c = contentColor ?: contentColorFor(bg)
    NavigationRail(
        modifier = modifier,
        containerColor = bg,
        contentColor = c,
        header = header,
        windowInsets = windowInsets ?: NavigationRailDefaults.windowInsets,
        content = content,
    )
}

@Composable
fun ColumnScope.DPNavigationRailItem(
    text: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedIcon: ImageVector = icon,
    alwaysShowLabel: Boolean = true,
    badgeText: String? = null,
    enabled: Boolean = true,
    colors: NavigationRailItemColors? = null,
) {
    val resolvedColors = colors ?: NavigationRailItemDefaults.colors()
    NavigationRailItem(
        selected = selected,
        onClick = onClick,
        icon = {
            if (badgeText != null) {
                BadgedBox(
                    badge = {
                        Badge { Text(badgeText) }
                    },
                ) {
                    Icon(
                        imageVector = if (selected) selectedIcon else icon,
                        contentDescription = null,
                    )
                }
            } else {
                Icon(
                    imageVector = if (selected) selectedIcon else icon,
                    contentDescription = null,
                )
            }
        },
        label = { DPTextView(text = text, variant = DPTextViewVariant.LabelSmall) },
        modifier = modifier,
        enabled = enabled,
        alwaysShowLabel = alwaysShowLabel,
        colors = resolvedColors,
    )
}

// -----------------------------------------------------------------------------
// Tabs
// -----------------------------------------------------------------------------

enum class DPTabRowVariant {
    Primary,
    Secondary,
    ScrollablePrimary,
    ScrollableSecondary,
}

@Composable
fun DPTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    variant: DPTabRowVariant = DPTabRowVariant.Primary,
    containerColor: Color? = null,
    contentColor: Color? = null,
    edgePadding: Dp = TabRowDefaults.ScrollableTabRowEdgeStartPadding,
    indicator: @Composable (TabIndicatorScope.() -> Unit)? = null,
    divider: @Composable () -> Unit = { HorizontalDivider() },
    tabs: @Composable () -> Unit,
) {
    val scrollState = rememberScrollState()
    val defaultPrimaryIndicator: @Composable TabIndicatorScope.() -> Unit = {
        TabRowDefaults.PrimaryIndicator(
            modifier = Modifier.tabIndicatorOffset(selectedTabIndex, matchContentSize = true),
            width = Dp.Unspecified,
        )
    }
    val defaultSecondaryIndicator: @Composable TabIndicatorScope.() -> Unit = {
        TabRowDefaults.SecondaryIndicator(
            Modifier.tabIndicatorOffset(selectedTabIndex, matchContentSize = false),
        )
    }
    val resolvedPrimaryIndicator = indicator ?: defaultPrimaryIndicator
    val resolvedSecondaryIndicator = indicator ?: defaultSecondaryIndicator
    when (variant) {
        DPTabRowVariant.Primary -> PrimaryTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = modifier,
            containerColor = containerColor ?: TabRowDefaults.primaryContainerColor,
            contentColor = contentColor ?: TabRowDefaults.primaryContentColor,
            indicator = resolvedPrimaryIndicator,
            divider = divider,
            tabs = tabs,
        )

        DPTabRowVariant.Secondary -> SecondaryTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = modifier,
            containerColor = containerColor ?: TabRowDefaults.secondaryContainerColor,
            contentColor = contentColor ?: TabRowDefaults.secondaryContentColor,
            indicator = resolvedSecondaryIndicator,
            divider = divider,
            tabs = tabs,
        )

        DPTabRowVariant.ScrollablePrimary -> PrimaryScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = modifier,
            scrollState = scrollState,
            containerColor = containerColor ?: TabRowDefaults.primaryContainerColor,
            contentColor = contentColor ?: TabRowDefaults.primaryContentColor,
            edgePadding = edgePadding,
            indicator = resolvedPrimaryIndicator,
            divider = divider,
            minTabWidth = TabRowDefaults.ScrollableTabRowMinTabWidth,
            tabs = tabs,
        )

        DPTabRowVariant.ScrollableSecondary -> SecondaryScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = modifier,
            scrollState = scrollState,
            containerColor = containerColor ?: TabRowDefaults.secondaryContainerColor,
            contentColor = contentColor ?: TabRowDefaults.secondaryContentColor,
            edgePadding = edgePadding,
            indicator = resolvedSecondaryIndicator,
            divider = divider,
            minTabWidth = TabRowDefaults.ScrollableTabRowMinTabWidth,
            tabs = tabs,
        )
    }
}

@Composable
fun DPTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    selectedContentColor: Color? = null,
    unselectedContentColor: Color? = null,
) {
    val sel = selectedContentColor ?: LocalContentColor.current
    val unsel = unselectedContentColor ?: sel
    if (icon == null) {
        Tab(
            selected = selected,
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            text = { DPTextView(text = text, variant = DPTextViewVariant.LabelMedium) },
            selectedContentColor = sel,
            unselectedContentColor = unsel,
        )
    } else {
        Tab(
            selected = selected,
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            text = { DPTextView(text = text, variant = DPTextViewVariant.LabelMedium) },
            icon = { Icon(imageVector = icon, contentDescription = null) },
            selectedContentColor = sel,
            unselectedContentColor = unsel,
        )
    }
}

@Composable
fun DPLeadingIconTab(
    text: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selectedContentColor: Color? = null,
    unselectedContentColor: Color? = null,
) {
    LeadingIconTab(
        selected = selected,
        onClick = onClick,
        text = { DPTextView(text = text, variant = DPTextViewVariant.LabelMedium) },
        icon = { Icon(imageVector = icon, contentDescription = null) },
        modifier = modifier,
        enabled = enabled,
        selectedContentColor = selectedContentColor ?: LocalContentColor.current,
        unselectedContentColor = unselectedContentColor ?: (selectedContentColor
            ?: LocalContentColor.current),
    )
}

// -----------------------------------------------------------------------------
// Drawer sheets (containers are kept as raw M3; sheets apply DPElevationLevel)
// -----------------------------------------------------------------------------

@Composable
fun DPModalDrawerSheet(
    modifier: Modifier = Modifier,
    drawerShape: Shape? = null,
    drawerContainerColor: Color? = null,
    drawerContentColor: Color? = null,
    drawerTonalElevation: DPElevationLevel = DPElevationLevel.Level1,
    windowInsets: WindowInsets? = null,
    content: @Composable ColumnScope.() -> Unit,
) = ModalDrawerSheet(
    modifier = modifier,
    drawerShape = drawerShape ?: DrawerDefaults.shape,
    drawerContainerColor = drawerContainerColor ?: DrawerDefaults.modalContainerColor,
    drawerContentColor = drawerContentColor
        ?: contentColorFor(drawerContainerColor ?: DrawerDefaults.modalContainerColor),
    drawerTonalElevation = drawerTonalElevation.value(),
    windowInsets = windowInsets ?: DrawerDefaults.windowInsets,
    content = content,
)

@Composable
fun DPDismissibleDrawerSheet(
    modifier: Modifier = Modifier,
    drawerShape: Shape? = null,
    drawerContainerColor: Color? = null,
    drawerContentColor: Color? = null,
    drawerTonalElevation: DPElevationLevel = DPElevationLevel.Level1,
    windowInsets: WindowInsets? = null,
    content: @Composable ColumnScope.() -> Unit,
) = DismissibleDrawerSheet(
    modifier = modifier,
    drawerShape = drawerShape ?: RectangleShape,
    drawerContainerColor = drawerContainerColor ?: DrawerDefaults.standardContainerColor,
    drawerContentColor = drawerContentColor
        ?: contentColorFor(drawerContainerColor ?: DrawerDefaults.standardContainerColor),
    drawerTonalElevation = drawerTonalElevation.value(),
    windowInsets = windowInsets ?: DrawerDefaults.windowInsets,
    content = content,
)

@Composable
fun DPPermanentDrawerSheet(
    modifier: Modifier = Modifier,
    drawerShape: Shape? = null,
    drawerContainerColor: Color? = null,
    drawerContentColor: Color? = null,
    drawerTonalElevation: DPElevationLevel = DPElevationLevel.Level1,
    windowInsets: WindowInsets? = null,
    content: @Composable ColumnScope.() -> Unit,
) = PermanentDrawerSheet(
    modifier = modifier,
    drawerShape = drawerShape ?: RectangleShape,
    drawerContainerColor = drawerContainerColor ?: DrawerDefaults.standardContainerColor,
    drawerContentColor = drawerContentColor
        ?: contentColorFor(drawerContainerColor ?: DrawerDefaults.standardContainerColor),
    drawerTonalElevation = drawerTonalElevation.value(),
    windowInsets = windowInsets ?: DrawerDefaults.windowInsets,
    content = content,
)

// -----------------------------------------------------------------------------
// Previews
// -----------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@DPComponentPreview
@Composable
private fun DPTopAppBarAllVariantsPreview() {
    Preview {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DPTheme.spacing.lg, vertical = DPTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(DPTheme.spacing.md),
        ) {
            DPTopAppBarVariant.entries.forEach { v ->
                DPTopAppBar(
                    title = "Title",
                    subtitle = "Subtitle",
                    variant = v,
                )
            }
        }
    }
}

@DPComponentPreview
@Composable
private fun DPNavigationBarPreview() {
    Preview {
        DPNavigationBar {
            DPNavigationBarItem(
                text = "Home",
                icon = Icons.Filled.Home,
                selected = true,
                onClick = {},
            )
            DPNavigationBarItem(
                text = "Mail",
                icon = Icons.Filled.Mail,
                selected = false,
                onClick = {},
            )
            DPNavigationBarItem(
                text = "Settings",
                icon = Icons.Filled.Settings,
                selected = false,
                badgeText = "3",
                onClick = {},
            )
        }
    }
}

@DPComponentPreview
@Composable
private fun DPTabRowPrimarySecondaryPreview() {
    Preview {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DPTheme.spacing.lg, vertical = DPTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(DPTheme.spacing.lg),
        ) {
            DPTabRow(selectedTabIndex = 0, variant = DPTabRowVariant.Primary) {
                DPTab("Tab 1", selected = true, onClick = {})
                DPTab("Tab 2", selected = false, onClick = {})
                DPTab("Tab 3", selected = false, onClick = {})
            }
            DPTabRow(selectedTabIndex = 1, variant = DPTabRowVariant.Secondary) {
                DPTab("Alpha", selected = true, onClick = {})
                DPTab("Beta", selected = false, onClick = {})
                DPTab("Gamma", selected = false, onClick = {})
            }
        }
    }
}
