package dev.shounakmulay.devpulse.feature.feed.interactor.feed

import androidx.paging.PagingData
import androidx.paging.map
import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.domain.feed.discovery.GetPaginatedFeedDiscoveryFeedsUseCase
import dev.shounakmulay.devpulse.core.domain.feed.discovery.ObserveFeedDiscoveryCollectionShelvesUseCase
import dev.shounakmulay.devpulse.core.domain.feed.discovery.ObserveFeedDiscoveryCollectionUseCase
import dev.shounakmulay.devpulse.core.domain.feed.discovery.ObserveFeedDiscoveryFeedUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.ExtractInitialsUseCase
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.feed.FeedDiscoveryCollection
import dev.shounakmulay.devpulse.core.domain.models.feed.FeedDiscoveryFeed
import dev.shounakmulay.devpulse.core.domain.models.feed.FeedDiscoveryImportState
import dev.shounakmulay.devpulse.core.domain.models.feed.FeedDiscoveryQuery
import dev.shounakmulay.devpulse.core.logging.DPLogger
import dev.shounakmulay.devpulse.core.ui.image.getWebsiteImageUrl
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import dev.shounakmulay.devpulse.feature.feed.model.UIDiscoveryCollection
import dev.shounakmulay.devpulse.feature.feed.model.UIDiscoveryCollectionShelf
import dev.shounakmulay.devpulse.feature.feed.model.UIDiscoveryFeed
import dev.shounakmulay.devpulse.feature.feed.model.UIDiscoveryFeedImportState
import dev.shounakmulay.devpulse.feature.feed.model.UIDiscoveryFeedSearchHighlights
import dev.shounakmulay.devpulse.feature.feed.text.parseFtsHighlightedText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

@Factory
class DiscoveryFeedInteractor(
    private val observeFeedDiscoveryCollectionShelvesUseCase: ObserveFeedDiscoveryCollectionShelvesUseCase,
    private val observeFeedDiscoveryCollectionUseCase: ObserveFeedDiscoveryCollectionUseCase,
    private val observeFeedDiscoveryFeedUseCase: ObserveFeedDiscoveryFeedUseCase,
    private val getPaginatedFeedDiscoveryFeedsUseCase: GetPaginatedFeedDiscoveryFeedsUseCase,
    private val extractInitialsUseCase: ExtractInitialsUseCase,
    private val dispatcherProvider: DispatcherProvider,
    logger: DPLogger,
) {
    private companion object {
        const val TAG = "DiscoveryFeedInteractor"
    }

    private val log = logger.withTag(TAG)

    fun observeCollectionShelves(): Flow<List<UIDiscoveryCollectionShelf>> =
        observeFeedDiscoveryCollectionShelvesUseCase().map { shelves ->
            shelves.map { shelf ->
                UIDiscoveryCollectionShelf(
                    collection = toUIDiscoveryCollection(shelf.collection),
                    feeds = shelf.feeds.map(::toUIDiscoveryFeed),
                )
            }
        }.flowOn(dispatcherProvider.defaultDispatcher)

    fun observeCollection(collectionId: UUID): Flow<UIDiscoveryCollection?> =
        observeFeedDiscoveryCollectionUseCase(collectionId).map { it?.let(::toUIDiscoveryCollection) }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeSelectedFeed(feedIds: Flow<UUID?>): Flow<Pair<UUID?, UIDiscoveryFeed?>> =
        feedIds.distinctUntilChanged().flatMapLatest { id ->
            log.d { "Discovery selected feed changed feedId=$id" }
            if (id == null) flowOf(null to null)
            else observeFeedDiscoveryFeedUseCase(id).map { feed -> id to feed?.let(::toUIDiscoveryFeed) }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getFeeds(queries: Flow<FeedDiscoveryQuery?>): Flow<PagingData<UIDiscoveryFeed>> {
        return queries.flatMapLatest { query ->
            if (query == null) flowOf(PagingData.empty())
            else getPaginatedFeedDiscoveryFeedsUseCase(query).map { pagingData ->
                pagingData.map(::toUIDiscoveryFeed)
            }
        }.flowOn(dispatcherProvider.defaultDispatcher)
    }

    private fun toUIDiscoveryFeed(feed: FeedDiscoveryFeed): UIDiscoveryFeed = UIDiscoveryFeed(
        id = feed.id,
        title = feed.title,
        description = feed.description?.let(TextResource::fromText),
        feedUrl = feed.url,
        websiteUrl = feed.websiteUrl,
        imageUrl = feed.iconUrl?.takeIf(String::isNotBlank)
            ?: getWebsiteImageUrl(link = feed.websiteUrl, sourceUrl = feed.url),
        initials = extractInitialsUseCase(feed.title),
        importState = when (feed.importState) {
            FeedDiscoveryImportState.Added -> UIDiscoveryFeedImportState.Added
            FeedDiscoveryImportState.Adding -> UIDiscoveryFeedImportState.Adding
            FeedDiscoveryImportState.Queued -> UIDiscoveryFeedImportState.Queued
            FeedDiscoveryImportState.Failed -> UIDiscoveryFeedImportState.Failed
            FeedDiscoveryImportState.NotAdded -> UIDiscoveryFeedImportState.NotAdded
        },
        searchHighlights = feed.search?.let { search ->
            UIDiscoveryFeedSearchHighlights(
                title = search.highlightedTitle?.let(::parseFtsHighlightedText),
                description = search.highlightedDescription?.let(::parseFtsHighlightedText),
                feedUrl = search.highlightedUrl?.let(::parseFtsHighlightedText),
                websiteUrl = search.highlightedWebsiteUrl?.let(::parseFtsHighlightedText),
            )
        },
    )

    private fun toUIDiscoveryCollection(collection: FeedDiscoveryCollection) = UIDiscoveryCollection(
        id = collection.id,
        title = collection.title,
        description = collection.description,
        feedCount = collection.feedCount,
    )
}
