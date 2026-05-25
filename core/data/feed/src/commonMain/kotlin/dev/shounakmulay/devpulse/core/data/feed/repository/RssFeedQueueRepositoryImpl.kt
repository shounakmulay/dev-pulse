package dev.shounakmulay.devpulse.core.data.feed.repository

import dev.shounakmulay.devpulse.core.data.db.dao.FeedQueueDao
import dev.shounakmulay.devpulse.core.data.feed.mapper.RssFeedQueueMapper
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

@Factory(binds = [RssFeedQueueRepository::class])
internal class RssFeedQueueRepositoryImpl(
    private val feedQueueDao: FeedQueueDao,
    private val feedQueueMapper: RssFeedQueueMapper
) : RssFeedQueueRepository {
    override suspend fun enqueue(entries: List<RssFeedQueueEntry>) {
        val localEntries = entries.map {
            feedQueueMapper.toLocalRssFeedQueue(it)
        }
        feedQueueDao.add(localEntries)
    }

    override suspend fun updateQueueStatus(
        id: Int,
        status: RssFeedQueueStatus
    ) {
        val localStatus = feedQueueMapper.toLocalRssFeedQueueStatus(status)
        feedQueueDao.updateStatus(id, localStatus)
    }

    override suspend fun getNextToProcess(): RssFeedQueueEntry? {
        return feedQueueDao.getNext()?.let {
            feedQueueMapper.fromLocalRssFeedQueue(it)
        }
    }

    override fun observeQueueForUrls(urls: List<String>): Flow<List<RssFeedQueueEntry>> {
        return feedQueueDao.observeEntriesForUrls(urls)
            .map {
                it.map(feedQueueMapper::fromLocalRssFeedQueue)
            }
    }

}
