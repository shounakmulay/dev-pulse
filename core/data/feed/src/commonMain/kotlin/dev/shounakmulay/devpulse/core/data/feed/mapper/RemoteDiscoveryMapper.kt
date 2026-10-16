package dev.shounakmulay.devpulse.core.data.feed.mapper

import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalFeedDiscoveryCollection
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalFeedDiscoveryCollectionToFeedMapping
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalFeedDiscoveryFeedFormat
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalFeedDiscoveryFeedMetadata
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalFeedDiscoveryMasterMetadata
import dev.shounakmulay.devpulse.core.data.feed.model.discovery.RemoteDiscoveryCatalog
import dev.shounakmulay.devpulse.core.data.feed.model.discovery.RemoteDiscoveryCollection
import dev.shounakmulay.devpulse.core.data.feed.model.discovery.RemoteDiscoveryCollectionMetadata
import dev.shounakmulay.devpulse.core.data.feed.model.discovery.RemoteDiscoveryFeed
import org.koin.core.annotation.Factory

@Factory
class RemoteDiscoveryMapper {
    fun toLocalFeedDiscoveryMasterMetadata(
        from: RemoteDiscoveryCatalog,
        etag: String? = null
    ) = LocalFeedDiscoveryMasterMetadata(
        catalogId = LocalUUID(from.catalogId),
        feedMembershipCount = from.feedMembershipCount,
        publication = from.publication,
        schemaVersion = from.schemaVersion,
        uniqueFeedCount = from.uniqueFeedCount,
        etag = etag
    )

    fun toLocalFeedDiscoveryCollection(
        from: RemoteDiscoveryCollectionMetadata,
        document: RemoteDiscoveryCollection,
        catalogId: LocalUUID
    ) = LocalFeedDiscoveryCollection(
        id = LocalUUID(from.id),
        catalogId = catalogId,
        path = from.path,
        feedCount = from.feedCount,
        sha256Hash = from.sha256,
        slug = from.slug,
        title = from.title,
        position = from.position,
        description = document.description
    )

    fun toLocalFeedDiscoveryFeedMetadata(from: RemoteDiscoveryFeed) =
        LocalFeedDiscoveryFeedMetadata(
            id = LocalUUID(from.id),
            title = from.title,
            description = from.description,
            feedFormat = when (from.feedFormat) {
                "atom" -> LocalFeedDiscoveryFeedFormat.ATOM
                "rss" -> LocalFeedDiscoveryFeedFormat.RSS
                "rss1" -> LocalFeedDiscoveryFeedFormat.RSS1
                else -> throw IllegalArgumentException("Unsupported discovery feed format: ${from.feedFormat}")
            },
            url = from.feedUrl,
            iconUrl = from.iconUrl,
            languageTag = from.languageTag,
            websiteUrl = from.websiteUrl
        )

    fun toLocalFeedDiscoveryCollectionToFeedMappings(
        from: RemoteDiscoveryCollection,
    ): List<LocalFeedDiscoveryCollectionToFeedMapping> {
        val collectionId = LocalUUID(from.id)
        return from.feeds.mapIndexed { position, feed ->
            LocalFeedDiscoveryCollectionToFeedMapping(
                collectionId = collectionId,
                feedId = LocalUUID(feed.id),
                position = position
            )
        }
    }
}
