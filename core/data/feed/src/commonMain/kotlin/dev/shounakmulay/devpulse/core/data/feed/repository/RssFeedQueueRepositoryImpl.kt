package dev.shounakmulay.devpulse.core.data.feed.repository

import dev.shounakmulay.devpulse.core.data.db.dao.FeedQueueDao
import dev.shounakmulay.devpulse.core.data.feed.mapper.RssFeedQueueMapper
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueStatus
import org.koin.core.annotation.Factory

@Factory(binds = [RssFeedQueueRepository::class])
internal class RssFeedQueueRepositoryImpl(
    private val feedQueueDao: FeedQueueDao,
    private val feedQueueMapper: RssFeedQueueMapper
) : RssFeedQueueRepository {
    override suspend fun enqueue(entry: RssFeedQueueEntry) {
        val localEntry = feedQueueMapper.toLocalRssFeedQueue(entry)
        feedQueueDao.add(listOf(localEntry))
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

}
