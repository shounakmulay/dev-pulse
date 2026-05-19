package dev.shounakmulay.devpulse.core.data.feed.repository

import com.prof18.rssparser.model.RssChannel
import com.prof18.rssparser.model.RssItem
import dev.shounakmulay.devpulse.core.data.db.dao.FeedContentDao
import dev.shounakmulay.devpulse.core.data.db.dao.FeedDao
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssContentFeedPost
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeed
import dev.shounakmulay.devpulse.core.data.feed.hook.ContentFeedPostFilterExistingHook
import dev.shounakmulay.devpulse.core.data.feed.hook.ContentFeedPostNormalisationHook
import dev.shounakmulay.devpulse.core.data.feed.hook.CoreBatchHook
import dev.shounakmulay.devpulse.core.data.feed.hook.model.PostWithIdentity
import dev.shounakmulay.devpulse.core.data.feed.identity.IdentityGenerator
import dev.shounakmulay.devpulse.core.data.feed.mapper.RssFeedMapper
import dev.shounakmulay.devpulse.core.data.feed.mapper.RssItemMapper
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.chunked
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.koin.core.annotation.Factory

@Factory
internal class RssContentFeedProcessor(
    private val identityGenerator: IdentityGenerator,
    private val filterExistingPostsHook: ContentFeedPostFilterExistingHook,
    private val postNormalisationHook: ContentFeedPostNormalisationHook,
    private val feedContentDao: FeedContentDao,
    private val feedDao: FeedDao,
    private val rssItemMapper: RssItemMapper,
    private val rssFeedMapper: RssFeedMapper
) {
    suspend fun process(url: String, rssChannel: RssChannel): Unit = coroutineScope {
        val localRssFeed = async {
            buildLocalRssFeed(
                url = url,
                rssChannel = rssChannel
            )
        }
        val localRssContentFeedPosts =
            async { buildLocalRssContentFeedPost(rssChannel, localRssFeed) }

        awaitAll(localRssFeed, localRssContentFeedPosts)
    }

    private fun getCoreBatchHooks(): List<CoreBatchHook<PostWithIdentity>> {
        return listOf(
            filterExistingPostsHook
        )
    }

    private fun getCoreItemHooks(): List<ContentFeedPostNormalisationHook> {
        return listOf(
            postNormalisationHook
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun buildLocalRssContentFeedPost(
        rssChannel: RssChannel,
        localRssFeed: Deferred<LocalRssFeed>
    ) {
        rssChannel.items.asFlow()
            .chunked(50)
            .map {
                processRssItemsChunk(rssItems = it, feed = localRssFeed)
            }.onEach {
                feedContentDao.upsertPosts(it)
            }
            .collect()
    }

    private suspend fun processRssItemsChunk(
        rssItems: List<RssItem>,
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
            val post = rssItemMapper.toLocalRssContentFeedPost(
                item = rssItem,
                feedId = feedId,
                existingIdentity = existingItems[fingerprint]
            )

            PostWithIdentity(
                post = post,
                identity = existingLocalIdentity
            )
        }

        return processCoreHooks(localPostsWithIdentity)
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
        rssItems: List<RssItem>,
        feedId: String
    ): Map<String, RssItem> {
        return rssItems.associateBy { rssItem ->
            val dataForFingerprint: List<String> = buildList {
                rssItem.guid?.let { add(it) }
                rssItem.sourceUrl?.let { add(it) }

                if (addFeedIdAndExit(feedId)) return@buildList

                rssItem.title?.let { add(it) }
                rssItem.pubDate?.let { add(it) }
                rssItem.author?.let { add(it) }

                if (addFeedIdAndExit(feedId)) return@buildList

                add(rssItem.hashCode().toString())
            }

            identityGenerator.generateFingerprint(*dataForFingerprint.toTypedArray())
        }
    }

    private fun MutableList<String>.addFeedIdAndExit(feedId: String): Boolean {
        if (isNotEmpty()) {
            add(0, feedId)
            return true
        }
        return false
    }

    private suspend fun buildLocalRssFeed(url: String, rssChannel: RssChannel): LocalRssFeed {
        val exitingIdentity = feedDao.getFeedIdentityBySourceUrl(url)
        return rssFeedMapper.toLocalRssFeed(
            url = url,
            from = rssChannel,
            existingIdentity = exitingIdentity
        )
    }
}