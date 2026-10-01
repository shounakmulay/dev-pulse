package dev.shounakmulay.devpulse.feature.feed.interactor.feed

import dev.shounakmulay.devpulse.core.domain.feed.feed.ExtractInitialsUseCase
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedSearchResult
import dev.shounakmulay.devpulse.feature.feed.interactor.getWebsiteImageUrl
import dev.shounakmulay.devpulse.feature.feed.model.UIFeedSearchResult
import dev.shounakmulay.devpulse.feature.feed.text.parseFtsHighlightedText
import org.koin.core.annotation.Factory

@Factory
class FeedSearchInteractor(
    private val extractInitialsUseCase: ExtractInitialsUseCase,
) {
    fun getUIFeedSearchResults(results: List<RssFeedSearchResult>?): List<UIFeedSearchResult> {
        if (results == null) return emptyList()
        return results.map {
            UIFeedSearchResult(
                id = it.id,
                title = parseFtsHighlightedText(it.highlightedName.ifBlank { it.highlightedTitle }),
                sourceUrl = it.sourceUrl,
                pinned = it.pinned,
                imageUrl = it.image?.url,
                description = parseFtsHighlightedText(it.highlightedDescription),
                websiteImageUrl = getWebsiteImageUrl(link = it.link, sourceUrl = it.sourceUrl),
                initials = extractInitialsUseCase(it.highlightedName.ifBlank { it.highlightedTitle }),
            )
        }
    }
}