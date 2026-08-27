package dev.shounakmulay.devpulse.core.data.feed.repository

import dev.shounakmulay.devpulse.core.data.db.transaction.DevPulseDatabaseTransactionAccessor
import dev.shounakmulay.devpulse.core.data.feed.mapper.RssFeedMapper
import dev.shounakmulay.devpulse.core.data.feed.mapper.RssPostMapper
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedIdentity
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueStatus
import dev.shounakmulay.devpulse.core.domain.models.feedSync.RssFeedSyncMetadata
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPost
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostWithExistingIdentity
import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeed
import org.koin.core.annotation.Factory

@Factory
class FeedSyncRepositoryImpl(
    private val postRepository: PostRepository,
    private val postCategoryRepository: PostCategoryRepository,
    private val feedRepository: FeedRepository,
    private val feedQueueRepository: RssFeedQueueRepository,
    private val feedSyncMetadataRepository: FeedSyncMetadataRepository,
    private val rssPostMapper: RssPostMapper,
    private val rssFeedMapper: RssFeedMapper,
    private val transactionAccessor: DevPulseDatabaseTransactionAccessor
) : FeedSyncRepository {
    override suspend fun importFeed(
        entry: RssFeedQueueEntry,
        parsedFeed: ParsedFeed,
        existingIdentity: RssFeedIdentity?
    ): RssFeed {
        val feed = rssFeedMapper.toRssFeed(
            queueEntry = entry,
            from = parsedFeed.metadata,
            existingIdentity = existingIdentity
        )
        feedRepository.upsertFeed(feed)
        return feed
    }

    override suspend fun onImportCompleted(
        entry: RssFeedQueueEntry,
        feed: RssFeed,
        savedCount: Int,
        metadata: RssFeedSyncMetadata,
        removeStaleEntries: Boolean
    ) = transactionAccessor.writeTransaction {
        feedQueueRepository.updateQueueEntry(entry.copy(status = RssFeedQueueStatus.COMPLETED))
        feedSyncMetadataRepository.upsertSyncMetadata(metadata)
        if (removeStaleEntries) {
            feedQueueRepository.removeStaleEntries(entry)
        }
    }

    override suspend fun onImportFailed(entry: RssFeedQueueEntry, metadata: RssFeedSyncMetadata?) {
        transactionAccessor.writeTransaction {
            feedQueueRepository.updateQueueEntry(
                entry.copy(
                    status = RssFeedQueueStatus.FAILED,
                    fetchAttempt = entry.fetchAttempt + 1
                )
            )
            metadata?.let { feedSyncMetadataRepository.upsertSyncMetadata(it) }
        }
    }

    override suspend fun removeStaleEntries(entry: RssFeedQueueEntry) {
        feedQueueRepository.removeStaleEntries(entry)
    }

    override suspend fun getFeedsDueForSync(): List<Pair<RssFeedSyncMetadata, RssFeedIdentity>> {
        return feedSyncMetadataRepository.getFeedsDueForSync()
    }

    override suspend fun importPosts(
        postsWithExistingIdentities: List<RssFeedPostWithExistingIdentity>
    ) {
        transactionAccessor.writeTransaction {
            val posts = postsWithExistingIdentities.map { it.post }
            postRepository.upsertPosts(posts)
            postCategoryRepository.upsertPostCategories(posts.toCategoryRows())
        }
    }

    private fun List<RssFeedPost>.toCategoryRows() = flatMap { post ->
        rssPostMapper.toRssPostCategories(
            postId = post.id.value,
            categories = post.categories
        )
    }
}