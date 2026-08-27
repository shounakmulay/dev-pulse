package dev.shounakmulay.devpulse.core.domain.feed.queue

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCatchingOnDefault
import dev.shounakmulay.devpulse.core.common.time.DateTimeProvider
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedRepository
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedSyncMetadataRepository
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedSyncRepository
import dev.shounakmulay.devpulse.core.data.feed.repository.ParsedFeedProcessor
import dev.shounakmulay.devpulse.core.data.feed.repository.PostRepository
import dev.shounakmulay.devpulse.core.data.feed.repository.RssFeedQueueRepository
import dev.shounakmulay.devpulse.core.domain.feed.queue.hooks.CorePostsBatchHook
import dev.shounakmulay.devpulse.core.domain.feed.queue.hooks.CorePostsItemHook
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedIdentity
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueStatus
import dev.shounakmulay.devpulse.core.domain.models.feedSync.RssFeedSyncMetadata
import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeed
import dev.shounakmulay.devpulse.core.logging.DPLogger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.chunked
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import kotlin.time.Duration.Companion.hours

@Single(binds = [RssFeedQueueExecutor::class])
internal class RssFeedQueueExecutorImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val feedRepository: FeedRepository,
    private val postRepository: PostRepository,
    private val feedSyncMetadataRepository: FeedSyncMetadataRepository,
    private val feedSyncRepository: FeedSyncRepository,
    private val feedQueueRepository: RssFeedQueueRepository,
    private val dateTimeProvider: DateTimeProvider,
    private val parsedFeedProcessor: ParsedFeedProcessor,
    logger: DPLogger
) : RssFeedQueueExecutor, KoinComponent {

    private val coreBatchHooks by lazy {
        getKoin().getAll<CorePostsBatchHook>()
    }
    private val coreItemHooks by lazy {
        getKoin().getAll<CorePostsItemHook>()
    }
    private val logger = logger.withTag(Tag)
    private val coroutineScope =
        CoroutineScope(SupervisorJob() + dispatcherProvider.defaultDispatcher)
    private val queueProcessingMutex = Mutex()
    private val trigger = Channel<Unit>(Channel.CONFLATED)

    init {
        trigger
            .consumeAsFlow()
            .onEach {
                dispatcherProvider.runCatchingOnDefault {
                    drain()
                }
            }
            .launchIn(coroutineScope)
    }

    override fun processQueue() {
        val result = trigger.trySend(Unit)
        if (result.isSuccess) {
            logger.d { "Queue trigger accepted" }
        } else {
            logger.w { "Queue trigger rejected reason=${result.exceptionOrNull()?.message ?: "unknown"}" }
        }
    }

    private suspend fun drain() = queueProcessingMutex.withLock {
        logger.d { "Queue drain started" }
        while (true) {
            val next = feedQueueRepository.getNextToProcess()
            if (next == null) {
                logger.d { "Queue drain found no pending item" }
                break
            }

            if (next.status !in listOf(
                    RssFeedQueueStatus.QUEUED,
                    RssFeedQueueStatus.PROCESSING
                )
            ) {
                logger.w {
                    "Queue entry skipped id=${next.id} action=${next.actionType} status=${next.status} source=${next.url.sourceSummary()}"
                }
                break
            }

            processEntry(next)
        }
        logger.d { "Queue drain finished" }
    }

    private suspend fun processEntry(entry: RssFeedQueueEntry) {
        val existingFeedIdentity = feedRepository.getFeedIdentityBySourceUrl(entry.url)
        val existingSyncMetadata = existingFeedIdentity?.id?.let {
            feedSyncMetadataRepository.getSyncMetadata(it)
        }

        try {
            processEntrySync(
                entry = entry,
                existingFeedIdentity = existingFeedIdentity,
                existingSyncMetadata = existingSyncMetadata
            )
        } catch (e: Exception) {
            processEntrySyncError(
                existingFeedIdentity = existingFeedIdentity,
                existingSyncMetadata = existingSyncMetadata,
                entry = entry,
                exception = e
            )
        }
    }

    private suspend fun processEntrySyncError(
        existingFeedIdentity: RssFeedIdentity?,
        existingSyncMetadata: RssFeedSyncMetadata?,
        entry: RssFeedQueueEntry,
        exception: Exception
    ) {
        val syncMetadata = createSyncFailureMetadata(
            existingFeedIdentity = existingFeedIdentity,
            existingMetadata = existingSyncMetadata,
        )
        feedSyncRepository.onImportFailed(
            entry = entry,
            metadata = syncMetadata,
        )
        logger.e(exception) {
            "Queue entry failed id=${entry.id} action=${entry.actionType} source=${entry.url.sourceSummary()}"
        }
        if (exception is CancellationException) {
            throw exception
        }
    }

    private suspend fun processEntrySync(
        entry: RssFeedQueueEntry,
        existingFeedIdentity: RssFeedIdentity?,
        existingSyncMetadata: RssFeedSyncMetadata?
    ) {
        logger.d {
            "Queue entry claimed id=${entry.id} action=${entry.actionType} status=${entry.status} source=${entry.url.sourceSummary()}"
        }
        feedQueueRepository.updateQueueEntry(entry.copy(status = RssFeedQueueStatus.PROCESSING))
        val parsedFeed = feedRepository.fetchRssFeed(entry)

        val feed = feedSyncRepository.importFeed(
            entry = entry,
            parsedFeed = parsedFeed,
            existingIdentity = existingFeedIdentity
        )

        val savedCount = savePosts(
            entry = entry,
            parsedFeed = parsedFeed,
            rssFeed = feed,
        )

        feedSyncRepository.onImportCompleted(
            entry = entry,
            feed = feed,
            savedCount = savedCount,
            metadata = createSyncSuccessMetadata(
                existingMetadata = existingSyncMetadata,
                feed = feed,
                parsedFeed = parsedFeed,
                savedCount = savedCount,
            )
        )
        logger.d {
            "Queue entry completed id=${entry.id} action=${entry.actionType} source=${entry.url.sourceSummary()}"
        }
    }

    private suspend fun createSyncSuccessMetadata(
        existingMetadata: RssFeedSyncMetadata?,
        feed: RssFeed,
        parsedFeed: ParsedFeed,
        savedCount: Int,
    ): RssFeedSyncMetadata {
        require(savedCount >= 0) {
            "Saved count must be greater than or equal to 0"
        }

        val emptyFetches = existingMetadata?.consecutiveEmptyFetches ?: 0
        val consecutiveEmptyFetches = computeConsecutiveEmptyFetches(emptyFetches, savedCount)

        return createSyncMetadata(
            feedId = feed.id,
            existingMetadata = existingMetadata,
            savedCount = savedCount,
            parsedFeed = parsedFeed,
            consecutiveEmptyFetches = consecutiveEmptyFetches,
            consecutiveFailures = 0
        )
    }

    private suspend fun createSyncFailureMetadata(
        existingMetadata: RssFeedSyncMetadata?,
        existingFeedIdentity: RssFeedIdentity?,
    ): RssFeedSyncMetadata? {
        val emptyFetches = existingMetadata?.consecutiveEmptyFetches ?: 0
        val failures = existingMetadata?.consecutiveFailures ?: 0
        val consecutiveFailures = failures + 1

        return createSyncMetadata(
            feedId = existingFeedIdentity?.id ?: return null,
            existingMetadata = existingMetadata,
            savedCount = 0,
            parsedFeed = null,
            consecutiveEmptyFetches = emptyFetches,
            consecutiveFailures = consecutiveFailures
        )
    }

    private fun computeNextEligibleFetch(computedInterval: Int): Long {
        return dateTimeProvider.timeInFuture(computedInterval.hours)
    }


    private suspend fun RssFeedQueueExecutorImpl.createSyncMetadata(
        feedId: UUID,
        existingMetadata: RssFeedSyncMetadata?,
        savedCount: Int,
        parsedFeed: ParsedFeed?,
        consecutiveEmptyFetches: Int,
        consecutiveFailures: Int
    ): RssFeedSyncMetadata {
        val latestPostPublishedAt = postRepository.getLatestPostPublishedTimeForFeed(feedId)
        val computedInterval = computeCheckIntervalHours(existingMetadata, savedCount)
        val nowMillis = dateTimeProvider.nowEpochMilliseconds()
        val nextEligibleFetch = computeNextEligibleFetch(computedInterval)

        if (existingMetadata != null) {
            return existingMetadata.copy(
                checkIntervalHours = computedInterval,
                consecutiveEmptyFetches = consecutiveEmptyFetches,
                consecutiveFailures = consecutiveFailures,
                lastFetchedAt = nowMillis,
                lastNewArticleAt = latestPostPublishedAt,
                nextEligibleFetchAt = nextEligibleFetch,
            )
        }

        return RssFeedSyncMetadata(
            feedId = feedId,
            checkIntervalHours = computedInterval,
            consecutiveEmptyFetches = consecutiveEmptyFetches,
            consecutiveFailures = consecutiveFailures,
            lastFetchedAt = dateTimeProvider.nowEpochMilliseconds(),
            lastNewArticleAt = latestPostPublishedAt,
            nextEligibleFetchAt = computeNextEligibleFetch(computedInterval),
            etag = parsedFeed?.metadata?.etag,
            lastModified = parsedFeed?.metadata?.lastBuildDate
        )
    }

    private fun computeCheckIntervalHours(
        existingMetadata: RssFeedSyncMetadata?,
        savedCount: Int = 0
    ): Int {
        val factor = existingMetadata?.consecutiveEmptyFetches ?: 1
        if (savedCount == 0) {
            return minOf(factor * 2, 24)
        }

        return maxOf(1, factor / 4)
    }

    private fun computeConsecutiveEmptyFetches(
        emptyFetches: Int,
        savedCount: Int = 0
    ): Int {
        if (savedCount == 0) {
            return emptyFetches + 1
        }

        return 0
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun savePosts(
        entry: RssFeedQueueEntry,
        parsedFeed: ParsedFeed,
        rssFeed: RssFeed,
    ): Int {
        var upsertedCount = 0
        val feedId = rssFeed.id
        parsedFeed.items
            .chunked(50)
            .map {
                val posts = parsedFeedProcessor.mapToPostsWithIdentity(
                    rssItems = it,
                    feedId = rssFeed.id
                )
                val batchProcessedPosts = coreBatchHooks
                    .fold(posts) { accumulator, hook ->
                        hook.process(actionType = entry.actionType, posts = accumulator)
                    }

                batchProcessedPosts
                    .map { postWithExistingIdentity ->
                        coreItemHooks.fold(postWithExistingIdentity) { acc, hook ->
                            hook.process(
                                actionType = entry.actionType,
                                post = acc
                            )
                        }
                    }
            }.onEach {
                if (it.isEmpty()) return@onEach

                feedSyncRepository.importPosts(it)
                upsertedCount += it.size
                logger.d {
                    "RSS content chunk upserted feedId=$feedId upsertedPostCount=${it.size}"
                }
            }
            .collect()
        return upsertedCount
    }

    override fun isProcessing(): Boolean {
        return queueProcessingMutex.isLocked
    }

    private fun String.sourceSummary(): String {
        val withoutScheme = substringAfter("://", this)
        val host =
            withoutScheme.substringBefore('/').substringBefore('?').takeIf { it.isNotBlank() }
        return "host=${host ?: take(80)}"
    }

    private companion object {
        const val Tag = "RssFeedQueueExecutor"
    }
}
