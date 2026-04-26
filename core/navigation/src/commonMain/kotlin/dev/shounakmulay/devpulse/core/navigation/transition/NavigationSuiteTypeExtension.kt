package dev.shounakmulay.devpulse.core.navigation.transition

import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType

private val sideNavTypes = setOf(
    NavigationSuiteType.NavigationRail,
    NavigationSuiteType.WideNavigationRailExpanded,
    NavigationSuiteType.WideNavigationRailCollapsed,
    NavigationSuiteType.NavigationDrawer
)

private val bottomNavTypes = setOf(
    NavigationSuiteType.NavigationBar,
    NavigationSuiteType.ShortNavigationBarMedium,
    NavigationSuiteType.ShortNavigationBarCompact
)

internal fun NavigationSuiteType.isSideNavigation() = this in sideNavTypes

internal fun NavigationSuiteType.isBottomNavigation() = this !in sideNavTypes