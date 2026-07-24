package dev.shounakmulay.devpulse.feature.feed.screens.postlist.ui

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import dev.shounakmulay.devpulse.feature.feed.interactor.post.PostFilterAndSort
import dev.shounakmulay.devpulse.feature.feed.interactor.post.PostFilterSortState
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class PostListScreenState(
    val isLoading: Boolean = true,
    override val postFilterSortState: PostFilterAndSort,
) : ScreenState, PostFilterSortState
