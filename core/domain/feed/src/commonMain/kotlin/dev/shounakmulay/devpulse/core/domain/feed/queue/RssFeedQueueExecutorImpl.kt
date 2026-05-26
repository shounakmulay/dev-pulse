package dev.shounakmulay.devpulse.core.domain.feed.queue

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.data.feed.repository.ContentFeedRepository
import dev.shounakmulay.devpulse.core.data.feed.repository.RssFeedQueueRepository
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueStatus
import dev.shounakmulay.devpulse.core.logging.DPLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Single

@Single(binds = [RssFeedQueueExecutor::class])
internal class RssFeedQueueExecutorImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val feedRepository: ContentFeedRepository,
    private val feedQueueRepository: RssFeedQueueRepository,
    logger: DPLogger
) : RssFeedQueueExecutor {
    private val logger = logger.withTag(Tag)
    private val coroutineScope =
        CoroutineScope(SupervisorJob() + dispatcherProvider.defaultDispatcher)
    private val queueProcessingMutex = Mutex()
    private val trigger = Channel<Unit>(Channel.CONFLATED)

    init {
        trigger
            .consumeAsFlow()
            .onEach { drain() }
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
        try {
            logger.d {
                "Queue entry claimed id=${entry.id} action=${entry.actionType} status=${entry.status} source=${entry.url.sourceSummary()}"
            }
            feedQueueRepository.updateQueueStatus(entry.id, RssFeedQueueStatus.PROCESSING)
            feedRepository.addRssFeed(entry)
            feedQueueRepository.updateQueueStatus(entry.id, RssFeedQueueStatus.COMPLETED)
            logger.d {
                "Queue entry completed id=${entry.id} action=${entry.actionType} source=${entry.url.sourceSummary()}"
            }
        } catch (e: Exception) {
            feedQueueRepository.updateQueueStatus(entry.id, RssFeedQueueStatus.FAILED)
            logger.e(e) {
                "Queue entry failed id=${entry.id} action=${entry.actionType} source=${entry.url.sourceSummary()}"
            }
        }
    }

    override fun isProcessing(): Boolean {
        return queueProcessingMutex.isLocked
    }

    private fun String.sourceSummary(): String {
        val withoutScheme = substringAfter("://", this)
        val host = withoutScheme.substringBefore('/').substringBefore('?').takeIf { it.isNotBlank() }
        return "host=${host ?: take(80)}"
    }

    private companion object {
        const val Tag = "RssFeedQueueExecutor"
    }
}
