package dev.shounakmulay.devpulse.core.navigation

import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
@Immutable
sealed interface Screen : NavKey {

    @Serializable
    data object DeveloperTools {

        @Serializable
        data object DesignSystemBoard : Screen
    }

    @Serializable
    data object Tabs : Screen {
        @Serializable
        data object Home : Screen

        @Serializable
        @Immutable
        data object Feed : Screen {
            @Serializable
            @Immutable
            data class FeedDetail(val id: Int) : Screen
        }

        @Serializable
        data object Time : Screen
    }


    @Serializable
    data object Monitors : Screen


    @Serializable
    data object Settings : Screen
}