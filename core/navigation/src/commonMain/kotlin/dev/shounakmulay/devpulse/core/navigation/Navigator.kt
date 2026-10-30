package dev.shounakmulay.devpulse.core.navigation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.snapshots.Snapshot
import androidx.navigation3.runtime.NavBackStack
import dev.shounakmulay.devpulse.core.logging.log.DPLogger
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter

@Immutable
class Navigator(
    val state: NavigationState,
    private val logger: DPLogger,
) {

    private val reselectEvents = MutableSharedFlow<Screen>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    fun reselectEvents(screen: Screen) = reselectEvents.filter { it == screen }

    fun onReselect(screen: Screen) {
        reselectEvents.tryEmit(screen)
    }

    fun navigate(screen: Screen, onRootStack: Boolean = false): Boolean {
        val currentRootScreen = state.rootStack.last()
        val isOnTabs = currentRootScreen == Screen.Tabs
        logAttempt(action = "navigate", screen = screen, onRootStack = onRootStack)
        if (onRootStack || !isOnTabs) {
            if (currentRootScreen == screen) {
                return logResult(
                    action = "navigate",
                    screen = screen,
                    stack = "root",
                    result = false,
                    reason = "alreadyCurrent",
                )
            }
            return logResult(
                action = "navigate",
                screen = screen,
                stack = "root",
                result = state.rootStack.add(screen),
            )
        }

        if (screen in state.tabsBackStacks.keys) {
            state.selectedTab = screen
            return logResult(
                action = "navigate",
                screen = screen,
                stack = "tab",
                result = true,
                reason = "selectedTab",
            )
        }

        val currentStack = state.tabsBackStacks[state.selectedTab]
            ?: return logResult(
                action = "navigate",
                screen = screen,
                stack = "tab",
                result = false,
                reason = "missingSelectedStack",
            )
        val currentTabsStackScreen = currentStack.last()
        if (currentTabsStackScreen == screen) {
            return logResult(
                action = "navigate",
                screen = screen,
                stack = "tab",
                result = false,
                reason = "alreadyCurrent",
            )
        }
        return logResult(
            action = "navigate",
            screen = screen,
            stack = "tab",
            result = state.tabsBackStacks[state.selectedTab]?.add(screen) ?: false,
        )
    }

    fun replace(screen: Screen, onRootStack: Boolean = false): Boolean {
        logAttempt(action = "replace", screen = screen, onRootStack = onRootStack)
        if (onRootStack) {
            state.rootStack[state.rootStack.size - 1] = screen
            return logResult(action = "replace", screen = screen, stack = "root", result = true)
        }

        if (screen in state.tabsBackStacks.keys) {
            return logResult(
                action = "replace",
                screen = screen,
                stack = "tab",
                result = false,
                reason = "tabRouteReplacementRejected",
            )
        }

        val selectedStack = state.tabsBackStacks[state.selectedTab]
            ?: return logResult(
                action = "replace",
                screen = screen,
                stack = "tab",
                result = false,
                reason = "missingSelectedStack",
            )
        selectedStack[selectedStack.size - 1] = screen
        return logResult(action = "replace", screen = screen, stack = "tab", result = true)
    }

    fun replaceOfSameType(
        screen: Screen,
        onRootStack: Boolean = false,
        addIfNotPresent: Boolean = true,
    ): Boolean {
        logAttempt(action = "replaceOfSameType", screen = screen, onRootStack = onRootStack)
        if (onRootStack) {
            val isSameType = state.rootStack.last()::class == screen::class

            if (isSameType) {
                return replace(screen, true)
            }

            if (addIfNotPresent) {
                return navigate(screen, true)
            }

            return logResult(
                action = "replaceOfSameType",
                screen = screen,
                stack = "root",
                result = false,
                reason = "differentType",
            )
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

        return logResult(
            action = "replaceOfSameType",
            screen = screen,
            stack = "tab",
            result = false,
            reason = "differentType",
        )
    }

    fun removeFromFirstOnAllStacks(
        predicate: (Screen) -> Boolean,
    ): Boolean = Snapshot.withMutableSnapshot {
        logger.d {
            "Navigation mutation action=removeFromFirstOnAllStacks " +
                "selectedTab=${state.selectedTab.summary()} rootSize=${state.rootStack.size}"
        }
        val rootStack = state.rootStack.removeFromFirst(predicate)
        val tabStacks = state.tabsBackStacks.map {
            it.value.removeFromFirst(predicate)
        }.reduce { acc, bool -> acc || bool }
        logger.d {
            "Navigation result action=removeFromFirstOnAllStacks " +
                "selectedTab=${state.selectedTab.summary()} rootChanged=$rootStack tabsChanged=$tabStacks " +
                "result=${rootStack || tabStacks}"
        }
        return rootStack || tabStacks
    }

    private fun NavBackStack<Screen>.removeFromFirst(predicate: (Screen) -> Boolean): Boolean {
        val startIndex = indexOfFirst(predicate)
        val stackName = if (this === state.rootStack) "root" else "tab"
        logger.d {
            "Navigation mutation action=removeFromFirst stack=$stackName " +
                "size=$size matchIndex=$startIndex entries=${joinToString { it.summary() }}"
        }
        if (startIndex < 0) {
            logger.d { "Navigation result action=removeFromFirst stack=$stackName result=false reason=noMatch" }
            return false
        }
        val screensToRemove = subList(startIndex, size)
        screensToRemove.clear()
        logger.d {
            "Navigation result action=removeFromFirst stack=$stackName " +
                "result=${screensToRemove.isNotEmpty()} remaining=${joinToString { it.summary() }}"
        }
        return screensToRemove.isNotEmpty()
    }

    fun navigateBack(): Boolean {
        logger.d {
            "Navigation mutation action=navigateBack " +
                "selectedTab=${state.selectedTab.summary()} rootTop=${
                    state.rootStack.last().summary()
                }"
        }
        if (state.rootStack.last() == Screen.Tabs) {
            return navigateBackTabs()
        }

        return logBackResult(
            stack = "root",
            result = state.rootStack.removeLastOrNull() != null,
        )
    }

    private fun navigateBackTabs(): Boolean {
        val stack = state.tabsBackStacks[state.selectedTab]
            ?: error("BackStack for ${state.selectedTab} not found.")

        if (stack.last() == state.selectedTab) {
            state.selectedTab = state.tabsStart
            return logBackResult(stack = "tab", result = true, reason = "resetToStartTab")
        }

        return logBackResult(
            stack = "tab",
            result = stack.removeLastOrNull() != null,
        )
    }

    private fun logAttempt(action: String, screen: Screen, onRootStack: Boolean) {
        logger.d {
            "Navigation mutation action=$action target=${screen.summary()} " +
                "selectedTab=${state.selectedTab.summary()} requestedStack=${if (onRootStack) "root" else "tab"}"
        }
    }

    private fun logResult(
        action: String,
        screen: Screen,
        stack: String,
        result: Boolean,
        reason: String? = null,
    ): Boolean {
        logger.d {
            "Navigation result action=$action target=${screen.summary()} " +
                "selectedTab=${state.selectedTab.summary()} stack=$stack result=$result" +
                reason.asLogSuffix()
        }
        return result
    }

    private fun logBackResult(stack: String, result: Boolean, reason: String? = null): Boolean {
        logger.d {
            "Navigation result action=navigateBack selectedTab=${state.selectedTab.summary()} " +
                "stack=$stack result=$result" +
                reason.asLogSuffix()
        }
        return result
    }

    private fun String?.asLogSuffix(): String = this?.let { " reason=$it" }.orEmpty()

    private fun Screen.summary(): String = this::class.simpleName ?: toString()
}
