package dev.shounakmulay.core.navigation

import androidx.compose.runtime.Immutable


@Immutable
class Navigator(val state: NavigationState) {
    fun navigate(screen: Screen, onRootStack: Boolean = false): Boolean {
        val isOnTabs = state.rootStack.last() == Screen.Tabs
        if (onRootStack || !isOnTabs) {
            return state.rootStack.add(screen)
        }

        if (screen in state.tabsBackStacks.keys) {
            state.selectedTab = screen
            return true
        }

        return state.tabsBackStacks[state.selectedTab]?.add(screen) ?: false
    }

    fun replace(screen: Screen, onRootStack: Boolean = false): Boolean {
        if (onRootStack) {
            state.rootStack[state.rootStack.size - 1] = screen
            return true
        }

        if (screen in state.tabsBackStacks.keys) {
            // replacing tab routes is done at the state level
            return false
        }

        val selectedStack = state.tabsBackStacks[state.selectedTab] ?: return false
        selectedStack[selectedStack.size - 1] = screen
        return true
    }

    fun replaceOfSameType(
        screen: Screen,
        onRootStack: Boolean = false,
        addIfNotPresent: Boolean = true
    ): Boolean {
        if (onRootStack) {
            val isSameType = state.rootStack.last()::class == screen::class

            if (isSameType) {
                return replace(screen, true)
            }

            if (addIfNotPresent) {
                return navigate(screen, true)
            }
        }

        val current = state.tabsBackStacks[state.selectedTab]?.last()
            ?: error("No screen found for ${state.selectedTab}")
        val isSameType = current::class == screen::class

        if (isSameType) {
            return replace(screen, false)
        }

        if (addIfNotPresent) {
            return navigate(screen, false)
        }

        return false
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