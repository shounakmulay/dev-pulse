package dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.ui

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import dev.shounakmulay.devpulse.feature.feed.model.UIFeed
import dev.shounakmulay.devpulse.feature.feed.model.UIPostSort
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Immutable
data class FeedDetailScreenState(
    val isLoading: Boolean = true,
    @Transient
    val feed: RssFeed? = null,
    val uiFeed: UIFeed? = null,
    @Transient
    val filters: ImmutableList<RssPostFilter> = persistentListOf(
        RssPostFilter.Bookmarked(null),
        RssPostFilter.PublishedRange(),
        RssPostFilter.Category(emptySet()),
        RssPostFilter.TagIdsAny(emptySet()),
    ),
    @Transient
    val sort: ImmutableList<UIPostSort> = persistentListOf(),
) : ScreenState
