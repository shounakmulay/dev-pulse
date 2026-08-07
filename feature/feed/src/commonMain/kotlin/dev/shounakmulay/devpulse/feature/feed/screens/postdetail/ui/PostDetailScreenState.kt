package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostContent
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostWithFeedIdentity
import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Immutable
data class PostDetailScreenState(
    val isLoading: Boolean = false,
    @Transient
    val post: RssPostWithFeedIdentity? = null,
    @Transient
    val content: RssFeedPostContent? = null
) : ScreenState
