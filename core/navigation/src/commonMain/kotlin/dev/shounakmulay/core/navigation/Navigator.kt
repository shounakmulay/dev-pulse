package dev.shounakmulay.core.navigation

import androidx.compose.runtime.Immutable


@Immutable
class Navigator(val state: NavigationState) {
    fun navigate(screen: Screen, onRootStack: Boolean): Boolean {
        if (onRootStack) {
            return state.rootStack.add(screen)
        }

        if (screen in state.tabsBackStacks.keys) {
            state.selectedTab = screen
            return true
        }

        return state.tabsBackStacks[state.selectedTab]?.add(screen) ?: false
    }

    fun navigateBack(): Boolean {
        if (state.rootStack.last() == Screen.Tabs) {
            return navigateBackTabs()
        }

        return state.rootStack.removeLastOrNull() != null
    }

    private fun navigateBackTabs(): Boolean {
        val stack = state.tabsBackStacks[state.selectedTab]
            ?: error("BackStack for ${state.selectedTab} not found.")

        if (stack.last() == state.selectedTab) {
            state.selectedTab = state.tabsStart
            return true
        }

        return stack.removeLastOrNull() != null
    }
}