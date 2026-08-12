package dev.shounakmulay.devpulse.core.navigation

import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavKey
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
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
            data object AddFeed : Screen

            @Serializable
            @Immutable
            data object FeedList : Screen

            @Serializable
            @Immutable
            data class PostList(val launchFor: PostListLaunchData) : Screen {
                @Serializable
                sealed interface PostListLaunchData {
                    @Serializable
                    data object All : PostListLaunchData
                }
            }

            @Serializable
            @Immutable
            data class PostDetail(val id: UUID) : Screen

            @Serializable
            @Immutable
            data class FeedDetail(val id: UUID) : Screen
        }

        @Serializable
        data object Time : Screen
    }


    @Serializable
    data object Monitors : Screen


    @Serializable
    data object Settings : Screen

    @Serializable
    data object AboutLibs : Screen

    @Serializable
    data class WebView(val url: String) : Screen
}
