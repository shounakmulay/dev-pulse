package dev.shounakmulay.devpulse.core.data.feed.repository

import dev.shounakmulay.devpulse.core.data.db.dao.FeedDao
import dev.shounakmulay.devpulse.core.data.db.dao.FeedPostDao
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID
import dev.shounakmulay.devpulse.core.data.feed.identity.IdentityGenerator
import dev.shounakmulay.devpulse.core.data.feed.mapper.RssFeedMapper
import dev.shounakmulay.devpulse.core.data.feed.mapper.RssPostMapper
import dev.shounakmulay.devpulse.core.data.feed.mapper.UuidMapper
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostWithExistingIdentity
import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeedItem
import dev.shounakmulay.devpulse.core.logging.DPLogger
import org.koin.core.annotation.Factory

@Factory
class ParsedFeedProcessor(
    private val identityGenerator: IdentityGenerator,
    private val feedPostDao: FeedPostDao,
    private val feedDao: FeedDao,
    private val rssPostMapper: RssPostMapper,
    private val rssFeedMapper: RssFeedMapper,
    private val uuidMapper: UuidMapper,
    logger: DPLogger
) {
    private val logger = logger.withTag(Tag)

    suspend fun mapToPostsWithIdentity(
        rssItems: List<ParsedFeedItem>,
        feedId: UUID
    ): List<RssFeedPostWithExistingIdentity> {
        val localFeedUUID = uuidMapper.fromUuid(feedId)
        val itemsWithFingerprint = generateParsedFeedFingerprints(
            rssItems = rssItems,
            feedId = localFeedUUID
        )
        val existingItems = feedPostDao.getByFingerprints(itemsWithFingerprint.keys)

        val postsWithIdentity = itemsWithFingerprint.map { (fingerprint, rssItem) ->
            val existingLocalIdentity = existingItems[fingerprint]
            val post = rssPostMapper.toLocalRssContentFeedPost(
                item = rssItem,
                feedId = localFeedUUID,
                fingerprint = fingerprint,
                existingIdentity = existingItems[fingerprint]
            )

            RssFeedPostWithExistingIdentity(
                post = rssPostMapper.toRssFeedPost(post),
                identity = existingLocalIdentity?.let { rssPostMapper.toRssFeedPostIdentity(it) }
            )
        }

        return postsWithIdentity
    }



    private fun generateParsedFeedFingerprints(
        rssItems: List<ParsedFeedItem>,
        feedId: LocalUUID
    ): Map<String, ParsedFeedItem> {
        return rssItems.associateBy { rssItem ->
            val dataForFingerprint: List<String> = buildList {
                rssItem.guid?.let { add(it) }
                rssItem.link?.let { add(it) }

                if (addFeedIdAndExit(feedId)) return@buildList

                rssItem.title?.let { add(it) }
                rssItem.pubDate?.let { add(it) }
                rssItem.author?.let { add(it) }

                if (addFeedIdAndExit(feedId)) return@buildList

                add(rssItem.stableFallbackIdentity())
            }

            identityGenerator.generateFingerprint(*dataForFingerprint.toTypedArray())
        }
    }

    private fun ParsedFeedItem.stableFallbackIdentity(): String {
        return listOfNotNull(
            description?.take(120),
            content?.take(120),
            image,
            audio,
            video,
            rawEnclosure?.url,
            rawMediaContent?.url
        ).joinToString(separator = "|")
    }

    private fun MutableList<String>.addFeedIdAndExit(feedId: LocalUUID): Boolean {
        if (isNotEmpty()) {
            add(0, feedId.value)
            return true
        }
        return false
    }



    private companion object {
        const val Tag = "RssContentFeedProcessor"
    }
}
