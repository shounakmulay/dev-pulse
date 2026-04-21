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
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
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
                subclass(Screen.Tabs::class, Screen.Tabs.serializer())
                subclass(Screen.Tabs.Home::class, Screen.Tabs.Home.serializer())
                subclass(Screen.Monitors::class, Screen.Monitors.serializer())
                subclass(Screen.Tabs.Feed::class, Screen.Tabs.Feed.serializer())
                subclass(
                    Screen.Tabs.Feed.FeedDetail::class,
                    Screen.Tabs.Feed.FeedDetail.serializer()
                )
                subclass(Screen.Tabs.Time::class, Screen.Tabs.Time.serializer())
                subclass(Screen.Settings::class, Screen.Settings.serializer())
                subclass(Screen.DeveloperTools::class, Screen.DeveloperTools.serializer())
                subclass(
                    Screen.DeveloperTools.DesignSystemBoard::class,
                    Screen.DeveloperTools.DesignSystemBoard.serializer()
                )
            }
        }
    }

    val tabsBackStacks = tabRoutes.associateWith { key ->
        @Suppress("UNCHECKED_CAST")
        rememberNavBackStack(
            configuration = serializersConfig,
            key
        ) as NavBackStack<Screen>
    }

    @Suppress("UNCHECKED_CAST")
    val rootBackStack =
        rememberNavBackStack(
            configuration = serializersConfig,
            rootStart
        ) as NavBackStack<Screen>

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
    val rootStack: NavBackStack<Screen>,
    val tabsStart: Screen,
    selectedTab: MutableState<Screen>,
    val tabsBackStacks: Map<Screen, NavBackStack<Screen>>
) {
    var selectedTab: Screen by selectedTab

    @Composable
    fun toEntries(entryProvider: (Screen) -> NavEntry<Screen>): ImmutableList<NavEntry<Screen>> {
        val tabStacksEntries = tabsBackStacks.mapValues { (_, stack) ->
            rememberDecoratedNavEntries(
                backStack = stack,
                entryDecorators = rememberNavEntryDecorators(),
                entryProvider = entryProvider
            )
        }

        val rootStackEntries = rememberDecoratedNavEntries(
            backStack = rootStack.filterNot { it == Screen.Tabs },
            entryDecorators = rememberNavEntryDecorators(),
            entryProvider = entryProvider
        ).associateBy { it.contentKey }

        val entries = buildList {
            rootStack.forEach { screen ->
                if (screen == Screen.Tabs) {
                    val tabEntries = tabStacksEntries.getTabEntries()
                    addAll(tabEntries)
                } else {
                    // Default content key is screen.toString().
                    val rootEntry = rootStackEntries[screen.toString()]
                    if (rootEntry != null) add(rootEntry)
                }
            }
        }
        return entries.toImmutableList()
    }

    @Composable
    private fun rememberNavEntryDecorators(): List<NavEntryDecorator<Screen>> {
        return listOf(
            rememberSaveableStateHolderNavEntryDecorator<Screen>(),
            rememberViewModelStoreNavEntryDecorator()
        )
    }

    private fun Map<Screen, List<NavEntry<Screen>>>.getTabEntries(): List<NavEntry<Screen>> {
        val tabScreens = getTabsInUse()

        val expandedTabStacks = tabScreens.flatMap {
            get(it)?.toList().orEmpty()
        }

        return expandedTabStacks
    }

    private fun getTabsInUse(): List<Screen> {
        val tabScreens = if (selectedTab == tabsStart) {
            listOf(tabsStart)
        } else {
            listOf(tabsStart, selectedTab)
        }
        return tabScreens
    }
}

