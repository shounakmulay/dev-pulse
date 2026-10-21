package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui

import dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui.model.ReaderVisitReadState
import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostContent
import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import dev.shounakmulay.devpulse.feature.feed.model.UIFeedPost
import dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui.model.PostDetailScreenSection
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Immutable
data class PostDetailScreenState(
    val readVisit: ReaderVisitReadState = ReaderVisitReadState(),
    val isLoading: Boolean = false,
    val selectedSection: PostDetailScreenSection = PostDetailScreenSection.RSS,
    @Transient
    val post: UIFeedPost? = null,
    @Transient
    val content: RssFeedPostContent? = null,
    @Transient
    val rssContent: RssFeedPostContent? = null
) : ScreenState
