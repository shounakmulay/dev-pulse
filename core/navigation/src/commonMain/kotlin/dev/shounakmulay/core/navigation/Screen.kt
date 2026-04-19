package dev.shounakmulay.core.navigation

import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
@Immutable
sealed interface Screen : NavKey {

    @Serializable
    data object DeveloperTools : Screen {

        @Serializable
        data object DesignSystemBoard : Screen
    }

    @Serializable
    data object Tabs : Screen {
        @Serializable
        data object Home : Screen

        @Serializable
        data object Feed : Screen

        @Serializable
        data object Time : Screen
    }


    @Serializable
    data object Monitors : Screen


    @Serializable
    data object Settings : Screen
}