package dev.shounakmulay.devpulse.core.data.feed.mapper

import dev.shounakmulay.devpulse.core.common.time.DateTimeProvider
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeedQueue
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeedQueueActionRequestor
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeedQueueActionType
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeedQueueStatus
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeedType
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueActionRequestor
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueActionType
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueStatus
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedType
import org.koin.core.annotation.Factory

@Factory
class RssFeedQueueMapper(
    private val dateTimeParser: DateTimeProvider
) {
    fun toLocalRssFeedQueue(from: RssFeedQueueEntry): LocalRssFeedQueue {
        return LocalRssFeedQueue(
            id = from.id,
            url = from.url,
            name = from.name,
            feedType = toLocalRssFeedType(from.feedType),
            actionType = toLocalRssFeedQueueActionType(from.actionType),
            requestor = toLocalRssFeedQueueActionRequestor(from.requestor),
            status = toLocalRssFeedQueueStatus(from.status),
            fetchAttempt = from.fetchAttempt,
            createAt = from.createdAt,
            updatedAt = dateTimeParser.now(),
        )
    }

    fun fromLocalRssFeedQueue(from: LocalRssFeedQueue): RssFeedQueueEntry {
        return RssFeedQueueEntry(
            id = from.id,
            url = from.url,
            name = from.name,
            feedType = fromLocalRssFeedType(from.feedType),
            actionType = fromLocalRssFeedQueueActionType(from.actionType),
            requestor = fromLocalRssFeedQueueActionRequestor(from.requestor),
            status = fromLocalRssFeedQueueStatus(from.status),
            fetchAttempt = from.fetchAttempt,
            createdAt = from.createAt,
            updatedAt = from.updatedAt,
        )
    }

    private fun toLocalRssFeedQueueActionRequestor(from: RssFeedQueueActionRequestor): LocalRssFeedQueueActionRequestor {
        return when (from) {
            RssFeedQueueActionRequestor.USER -> LocalRssFeedQueueActionRequestor.USER
            RssFeedQueueActionRequestor.APP -> LocalRssFeedQueueActionRequestor.APP
        }
    }

    private fun fromLocalRssFeedQueueActionRequestor(from: LocalRssFeedQueueActionRequestor): RssFeedQueueActionRequestor {
        return when (from) {
            LocalRssFeedQueueActionRequestor.USER -> RssFeedQueueActionRequestor.USER
            LocalRssFeedQueueActionRequestor.APP -> RssFeedQueueActionRequestor.APP
        }
    }

    private fun toLocalRssFeedQueueActionType(from: RssFeedQueueActionType): LocalRssFeedQueueActionType {
        return when (from) {
            RssFeedQueueActionType.IMPORT -> LocalRssFeedQueueActionType.IMPORT
            RssFeedQueueActionType.SYNC -> LocalRssFeedQueueActionType.SYNC
        }
    }

    private fun fromLocalRssFeedQueueActionType(from: LocalRssFeedQueueActionType): RssFeedQueueActionType {
        return when (from) {
            LocalRssFeedQueueActionType.IMPORT -> RssFeedQueueActionType.IMPORT
            LocalRssFeedQueueActionType.SYNC -> RssFeedQueueActionType.SYNC
        }
    }

    fun toLocalRssFeedQueueStatus(from: RssFeedQueueStatus): LocalRssFeedQueueStatus {
        return when (from) {
            RssFeedQueueStatus.COMPLETED -> LocalRssFeedQueueStatus.COMPLETED
            RssFeedQueueStatus.FAILED -> LocalRssFeedQueueStatus.FAILED
            RssFeedQueueStatus.QUEUED -> LocalRssFeedQueueStatus.QUEUED
            RssFeedQueueStatus.PROCESSING -> LocalRssFeedQueueStatus.PROCESSING
        }
    }

    private fun fromLocalRssFeedQueueStatus(from: LocalRssFeedQueueStatus): RssFeedQueueStatus {
        return when (from) {
            LocalRssFeedQueueStatus.COMPLETED -> RssFeedQueueStatus.COMPLETED
            LocalRssFeedQueueStatus.FAILED -> RssFeedQueueStatus.FAILED
            LocalRssFeedQueueStatus.QUEUED -> RssFeedQueueStatus.QUEUED
            LocalRssFeedQueueStatus.PROCESSING -> RssFeedQueueStatus.PROCESSING
        }
    }

    private fun toLocalRssFeedType(from: RssFeedType): LocalRssFeedType {
        return when (from) {
            RssFeedType.CONTENT -> LocalRssFeedType.CONTENT
        }
    }

    private fun fromLocalRssFeedType(from: LocalRssFeedType): RssFeedType {
        return when (from) {
            LocalRssFeedType.CONTENT -> RssFeedType.CONTENT
        }
    }
}