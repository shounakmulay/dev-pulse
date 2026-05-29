package dev.shounakmulay.devpulse.core.data.feed.repository

import dev.shounakmulay.devpulse.core.data.db.dao.FeedContentDao
import dev.shounakmulay.devpulse.core.data.db.dao.FeedDao
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssContentFeedPost
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeed
import dev.shounakmulay.devpulse.core.data.feed.hook.ContentFeedPostFilterExistingHook
import dev.shounakmulay.devpulse.core.data.feed.hook.ContentFeedPostSanitizationHook
import dev.shounakmulay.devpulse.core.data.feed.hook.CoreBatchHook
import dev.shounakmulay.devpulse.core.data.feed.hook.CoreItemHook
import dev.shounakmulay.devpulse.core.data.feed.hook.model.PostWithIdentity
import dev.shounakmulay.devpulse.core.data.feed.identity.IdentityGenerator
import dev.shounakmulay.devpulse.core.data.feed.mapper.RssFeedMapper
import dev.shounakmulay.devpulse.core.data.feed.mapper.RssPostMapper
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeed
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedItem
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.logging.DPLogger
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.chunked
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.koin.core.annotation.Factory

@Factory
internal class RssContentFeedProcessor(
    private val identityGenerator: IdentityGenerator,
    private val filterExistingPostsHook: ContentFeedPostFilterExistingHook,
    private val postSanitizationHook: ContentFeedPostSanitizationHook,
    private val feedContentDao: FeedContentDao,
    private val feedDao: FeedDao,
    private val rssPostMapper: RssPostMapper,
    private val rssFeedMapper: RssFeedMapper,
    logger: DPLogger
) {
    private val logger = logger.withTag(Tag)

    suspend fun process(entry: RssFeedQueueEntry, parsedFeed: ParsedFeed): Unit = coroutineScope {
        logger.d {
            "RSS content processing started queueId=${entry.id} source=${entry.url.sourceSummary()} itemCount=${parsedFeed.itemCount}"
        }
        val localRssFeed = async {
            buildLocalRssFeed(
                entry = entry,
                parsedFeed = parsedFeed
            )
        }
        val upsertedPostCount =
            async { buildLocalRssContentFeedPost(parsedFeed, localRssFeed) }

        awaitAll(localRssFeed, upsertedPostCount)
        val finalUpsertedPostCount = upsertedPostCount.await()
        logger.d {
            "RSS content processing finished queueId=${entry.id} " +
                "source=${entry.url.sourceSummary()} upsertedPostCount=$finalUpsertedPostCount"
        }
    }

    private fun getCoreBatchHooks(): List<CoreBatchHook<PostWithIdentity>> {
        return listOf(
            filterExistingPostsHook
        )
    }

    private fun getCoreItemHooks(): List<CoreItemHook<PostWithIdentity>> {
        return listOf(
            postSanitizationHook
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun buildLocalRssContentFeedPost(
        parsedFeed: ParsedFeed,
        localRssFeed: Deferred<LocalRssFeed>
    ): Int {
        var upsertedCount = 0
        val feedId = localRssFeed.await().id
        parsedFeed.items
            .chunked(50)
            .map {
                processRssItemsChunk(rssItems = it, feed = localRssFeed)
            }.onEach {
                feedContentDao.upsertPosts(it)
                upsertedCount += it.size
                logger.d {
                    "RSS content chunk upserted feedId=$feedId upsertedPostCount=${it.size}"
                }
            }
            .collect()
        return upsertedCount
    }

    private suspend fun processRssItemsChunk(
        rssItems: List<ParsedFeedItem>,
        feed: Deferred<LocalRssFeed>
    ): List<LocalRssContentFeedPost> {
        val feed = feed.await()
        val feedId = feed.id
        val itemsWithFingerprint = generateFingerprints(
            rssItems = rssItems,
            feedId = feedId
        )
        val existingItems = feedContentDao.getByFingerprints(itemsWithFingerprint.keys)

        val localPostsWithIdentity = itemsWithFingerprint.map { (fingerprint, rssItem) ->
            val existingLocalIdentity = existingItems[fingerprint]
            val post = rssPostMapper.toLocalRssContentFeedPost(
                item = rssItem,
                feedId = feedId,
                fingerprint = fingerprint,
                existingIdentity = existingItems[fingerprint]
            )

            PostWithIdentity(
                post = post,
                identity = existingLocalIdentity
            )
        }

        val processedPosts = processCoreHooks(localPostsWithIdentity)
        logger.d {
            "RSS content chunk processed feedId=$feedId rawItemCount=${rssItems.size} " +
                "existingMatchCount=${existingItems.size} " +
                "filteredCount=${localPostsWithIdentity.size - processedPosts.size} " +
                "postCount=${processedPosts.size}"
        }
        return processedPosts
    }

    private suspend fun processCoreHooks(
        localPostsWithIdentity: List<PostWithIdentity>
    ): List<LocalRssContentFeedPost> {
        val batchHooks = getCoreBatchHooks()
        val batchProcessedPosts = batchHooks.fold(localPostsWithIdentity) { accumulator, hook ->
            hook.process(accumulator)
        }

        val itemHooks = getCoreItemHooks()
        return batchProcessedPosts
            .map {
                itemHooks.fold(it) { acc, hook ->
                    hook.process(acc)
                }
            }
            .map { it.post }
    }

    private fun generateFingerprints(
        rssItems: List<ParsedFeedItem>,
        feedId: String
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
            ordinal.toString(),
            description?.take(120),
            content?.take(120),
            image,
            audio,
            video,
            rawEnclosure?.url,
            rawMediaContent?.url
        ).joinToString(separator = "|")
    }

    private fun MutableList<String>.addFeedIdAndExit(feedId: String): Boolean {
        if (isNotEmpty()) {
            add(0, feedId)
            return true
        }
        return false
    }

    private suspend fun buildLocalRssFeed(
        entry: RssFeedQueueEntry,
        parsedFeed: ParsedFeed
    ): LocalRssFeed {
        val exitingIdentity = feedDao.getFeedIdentityBySourceUrl(entry.url)
        val localFeed = rssFeedMapper.toLocalRssFeed(
            queueEntry = entry,
            from = parsedFeed.metadata,
            existingIdentity = exitingIdentity
        )
        feedDao.upsertFeed(localFeed)
        logger.d {
            "RSS feed upserted feedId=${localFeed.id} source=${entry.url.sourceSummary()} existing=${exitingIdentity != null}"
        }
        return localFeed
    }

    private fun String.sourceSummary(): String {
        val withoutScheme = substringAfter("://", this)
        val host = withoutScheme.substringBefore('/').substringBefore('?').takeIf { it.isNotBlank() }
        return "host=${host ?: take(80)}"
    }

    private companion object {
        const val Tag = "RssContentFeedProcessor"
    }
}
