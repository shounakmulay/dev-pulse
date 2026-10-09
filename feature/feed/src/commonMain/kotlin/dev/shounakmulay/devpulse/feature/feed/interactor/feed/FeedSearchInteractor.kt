package dev.shounakmulay.devpulse.feature.feed.interactor.feed

import androidx.paging.PagingData
import androidx.paging.map
import dev.shounakmulay.devpulse.core.domain.feed.feed.ExtractInitialsUseCase
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedSearchResult
import dev.shounakmulay.devpulse.feature.feed.interactor.getWebsiteImageUrl
import dev.shounakmulay.devpulse.feature.feed.model.UIFeed
import dev.shounakmulay.devpulse.feature.feed.model.UIFeedSearchHighlights
import dev.shounakmulay.devpulse.feature.feed.text.parseFtsHighlightedText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

@Factory
class FeedSearchInteractor(
    private val extractInitialsUseCase: ExtractInitialsUseCase,
) {
    fun getUIFeedSearchFlow(from: Flow<PagingData<RssFeedSearchResult>>): Flow<PagingData<UIFeed>> =
        from.map { pagingData -> pagingData.map(::toUIFeed) }

    fun toUIFeed(feed: RssFeedSearchResult): UIFeed {
        val title = feed.name.ifBlank { feed.title }
        return UIFeed(
            id = feed.id,
            title = title,
            sourceUrl = feed.sourceUrl,
            pinned = feed.pinned,
            imageUrl = feed.image?.url,
            websiteImageUrl = getWebsiteImageUrl(link = feed.link, sourceUrl = feed.sourceUrl),
            initials = extractInitialsUseCase(title),
            searchHighlights = UIFeedSearchHighlights(
                highlightedTitle = feed.highlightedTitle?.let(::parseFtsHighlightedText),
                highlightedName = feed.highlightedName
                    ?.takeIf { feed.name.isNotBlank() }
                    ?.let(::parseFtsHighlightedText),
                highlightedDescription = feed.highlightedDescription?.let(::parseFtsHighlightedText),
            ),
        )
    }
}
