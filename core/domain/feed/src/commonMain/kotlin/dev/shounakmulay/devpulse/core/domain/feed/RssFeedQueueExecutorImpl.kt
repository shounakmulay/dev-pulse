package dev.shounakmulay.devpulse.core.domain.feed

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.data.feed.repository.ContentFeedRepository
import dev.shounakmulay.devpulse.core.data.feed.repository.RssFeedQueueRepository
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Single

@Single
internal class RssFeedQueueExecutorImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val feedRepository: ContentFeedRepository,
    private val feedQueueRepository: RssFeedQueueRepository
) : RssFeedQueueExecutor {
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
        trigger.trySend(Unit)
    }

    private suspend fun drain() = queueProcessingMutex.withLock {
        while (true) {
            val next = feedQueueRepository.getNextToProcess() ?: break

            if (next.status !in listOf(
                    RssFeedQueueStatus.QUEUED,
                    RssFeedQueueStatus.PROCESSING
                )
            ) {
                break
            }

            processEntry(next)
        }
    }

    private suspend fun processEntry(entry: RssFeedQueueEntry) {
        try {
            feedQueueRepository.updateQueueStatus(entry.id, RssFeedQueueStatus.PROCESSING)
            feedRepository.addRssFeed(entry)
            feedQueueRepository.updateQueueStatus(entry.id, RssFeedQueueStatus.COMPLETED)
        } catch (_: Exception) {
            feedQueueRepository.updateQueueStatus(entry.id, RssFeedQueueStatus.FAILED)
        }
    }

    override fun isProcessing(): Boolean {
        return queueProcessingMutex.isLocked
    }
}
