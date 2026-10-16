package dev.shounakmulay.devpulse.core.data.feed.mapper

import dev.shounakmulay.devpulse.core.data.db.model.feed.enums.LocalRssFeedQueueStatus
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalFeedDiscoveryFeed
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalFeedDiscoveryCollection
import dev.shounakmulay.devpulse.core.data.db.query.FtsQuerySanitizer
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedDiscoveryQuery
import dev.shounakmulay.devpulse.core.domain.models.feed.FeedDiscoveryCollection
import dev.shounakmulay.devpulse.core.domain.models.feed.FeedDiscoveryFeed
import dev.shounakmulay.devpulse.core.domain.models.feed.FeedDiscoveryImportState
import dev.shounakmulay.devpulse.core.domain.models.feed.FeedDiscoveryQuery
import dev.shounakmulay.devpulse.core.domain.models.feed.FeedDiscoverySearchHighlights
import org.koin.core.annotation.Factory

@Factory
class FeedDiscoveryMapper(
    private val uuidMapper: UuidMapper,
    private val ftsQuerySanitizer: FtsQuerySanitizer,
) {
    fun toLocalFeedDiscoveryQuery(query: FeedDiscoveryQuery): LocalFeedDiscoveryQuery? {
        return when (query) {
            is FeedDiscoveryQuery.Collection -> LocalFeedDiscoveryQuery.Collection(
                collectionId = uuidMapper.fromUuid(query.collectionId)
            )
            is FeedDiscoveryQuery.Search -> {
                val text = ftsQuerySanitizer.sanitize(query.text)
                if (text.isBlank()) return null
                LocalFeedDiscoveryQuery.Search(
                    text = text,
                    collectionId = query.collectionId?.let(uuidMapper::fromUuid),
                )
            }
        }
    }

    fun toFeedDiscoveryCollection(from: LocalFeedDiscoveryCollection) = FeedDiscoveryCollection(
        id = uuidMapper.toUuid(from.id),
        title = from.title,
        description = from.description,
        feedCount = from.feedCount,
    )

    fun toFeedDiscoveryFeed(from: LocalFeedDiscoveryFeed) = FeedDiscoveryFeed(
        id = uuidMapper.toUuid(from.metadata.id),
        title = from.metadata.title,
        description = from.metadata.description,
        url = from.metadata.url,
        websiteUrl = from.metadata.websiteUrl,
        iconUrl = from.metadata.iconUrl,
        importState = if (from.subscribed) FeedDiscoveryImportState.Added else when (from.importStatus) {
            LocalRssFeedQueueStatus.PROCESSING -> FeedDiscoveryImportState.Adding
            LocalRssFeedQueueStatus.QUEUED -> FeedDiscoveryImportState.Queued
            LocalRssFeedQueueStatus.FAILED -> FeedDiscoveryImportState.Failed
            LocalRssFeedQueueStatus.COMPLETED, null -> FeedDiscoveryImportState.NotAdded
        },
        search = from.search?.let { search ->
            FeedDiscoverySearchHighlights(
                highlightedTitle = search.highlightedTitle,
                highlightedDescription = search.highlightedDescription,
                highlightedUrl = search.highlightedUrl,
                highlightedWebsiteUrl = search.highlightedWebsiteUrl,
            )
        },
    )
}
