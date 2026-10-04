package dev.shounakmulay.devpulse.feature.feed.screens.postlist.ui

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.domain.models.post.FeedsPostListItemVariant
import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class PostListScreenState(
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val feedPostListItemVariant: FeedsPostListItemVariant = FeedsPostListItemVariant.DEFAULT
) : ScreenState
