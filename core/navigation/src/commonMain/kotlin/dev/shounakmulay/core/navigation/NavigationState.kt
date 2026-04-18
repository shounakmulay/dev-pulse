package dev.shounakmulay.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.serializer

@Composable
fun rememberNavigationState(
    rootStart: Screen,
    tabsStart: Screen,
    tabRoutes: Set<Screen>
): NavigationState {
    val selectedTab = rememberSerializable(
        tabsStart,
        tabRoutes,
        serializer = MutableStateSerializer(serializer<Screen>())
    ) {
        mutableStateOf(tabsStart)
    }

    val serializersConfig = SavedStateConfiguration {
        serializersModule = SerializersModule {
            polymorphic(NavKey::class) {
                subclass(Screen.TabsScreen::class, Screen.TabsScreen.serializer())
                subclass(Screen.HomeScreen::class, Screen.HomeScreen.serializer())
                subclass(Screen.MonitorsScreen::class, Screen.MonitorsScreen.serializer())
                subclass(Screen.FeedScreen::class, Screen.FeedScreen.serializer())
                subclass(Screen.TimeScreen::class, Screen.TimeScreen.serializer())
                subclass(Screen.SettingsScreen::class, Screen.SettingsScreen.serializer())
            }
        }
    }

    val tabsBackStacks = tabRoutes.associateWith { key ->
        rememberNavBackStack(
            configuration = serializersConfig,
            key
        )
    }

    val rootBackStack = rememberNavBackStack(configuration = serializersConfig, rootStart)

    return remember {
        NavigationState(
            rootStack = rootBackStack,
            tabsStart = tabsStart,
            selectedTab = selectedTab,
            tabsBackStacks = tabsBackStacks
        )
    }
}

class NavigationState(
    val rootStack: NavBackStack<NavKey>,
    val tabsStart: Screen,
    selectedTab: MutableState<Screen>,
    val tabsBackStacks: Map<Screen, NavBackStack<NavKey>>
) {
    var selectedTab: Screen by selectedTab

    @Composable
    fun toEntries(entryProvider: (NavKey) -> NavEntry<NavKey>): List<NavEntry<NavKey>> {
        val entries = buildList {
            rootStack.forEach {
                if (it == Screen.TabsScreen) {
                    addTabsScreens()
                } else {
                    add(it)
                }
            }
        }

        val decorator = listOf(
            rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
            rememberViewModelStoreNavEntryDecorator()
        )

        return rememberDecoratedNavEntries(
            backStack = entries,
            entryDecorators = decorator,
            entryProvider = entryProvider
        )
    }

    private fun MutableList<NavKey>.addTabsScreens() {
        val tabScreens = if (selectedTab == tabsStart) {
            listOf(tabsStart)
        } else {
            listOf(tabsStart, selectedTab)
        }

        val expandedTabStacks = tabScreens.flatMap {
            tabsBackStacks[it]?.toList().orEmpty()
        }

        addAll(expandedTabStacks)
    }
}