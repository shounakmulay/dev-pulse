package dev.shounakmulay.core.navigation

import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
@Immutable
sealed interface Screen : NavKey {

    @Serializable
    data object TabsScreen : Screen

    @Serializable
    data object HomeScreen : Screen

    @Serializable
    data object MonitorsScreen : Screen

    @Serializable
    data object FeedScreen : Screen

    @Serializable
    data object TimeScreen : Screen

    @Serializable
    data object SettingsScreen : Screen
}