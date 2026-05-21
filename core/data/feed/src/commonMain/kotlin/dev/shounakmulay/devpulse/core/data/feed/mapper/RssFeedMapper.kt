package dev.shounakmulay.devpulse.core.data.feed.mapper

import com.prof18.rssparser.model.RssChannel
import com.prof18.rssparser.model.RssImage
import com.prof18.rssparser.model.YoutubeChannelData
import dev.shounakmulay.devpulse.core.common.time.DateTimeProvider
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeed
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeedImage
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeedYoutubeChannel
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssFeedIdentity
import dev.shounakmulay.devpulse.core.data.feed.identity.IdentityGenerator
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedImage
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedYoutubeChannel
import org.koin.core.annotation.Factory
import kotlin.time.Clock

@Factory
class RssFeedMapper(
    private val identityGenerator: IdentityGenerator,
    private val dateTimeParser: DateTimeProvider
) {

    fun toLocalRssFeed(
        from: RssChannel,
        existingIdentity: LocalRssFeedIdentity?,
        queueEntry: RssFeedQueueEntry
    ): LocalRssFeed {
        val now = Clock.System.now().toEpochMilliseconds()
        return LocalRssFeed(
            id = existingIdentity?.id ?: identityGenerator.generateSortableId(),
            name = queueEntry.name,
            sourceUrl = queueEntry.url,
            title = from.title,
            link = from.link,
            description = from.description,
            image = from.image?.let { toLocalRssFeedImage(it) },
            lastBuildDate = from.lastBuildDate?.let { dateTimeParser.parseEpochMilliseconds(it) },
            updatePeriod = from.updatePeriod,
            youtubeChannel = from.youtubeChannelData?.let { toLocalRssFeedYoutubeChannel(it) },
            createdAt = existingIdentity?.createdAt ?: now,
            updatedAt = now,
        )
    }

    private fun toLocalRssFeedYoutubeChannel(from: YoutubeChannelData) = LocalRssFeedYoutubeChannel(
        channelId = from.channelId,
    )

    private fun toLocalRssFeedImage(from: RssImage) = LocalRssFeedImage(
        title = from.title,
        url = from.url,
        link = from.link,
        description = from.description,
    )

    fun toRssFeed(from: LocalRssFeed) = RssFeed(
        id = from.id,
        sourceUrl = from.sourceUrl,
        title = from.title,
        link = from.link,
        description = from.description,
        image = from.image?.toRssFeedImage(),
        lastBuildDate = from.lastBuildDate,
        updatePeriod = from.updatePeriod,
        youtubeChannel = from.youtubeChannel?.toRssFeedYoutubeChannel(),
        createdAt = from.createdAt,
        updatedAt = from.updatedAt
    )

    private fun LocalRssFeedImage.toRssFeedImage() = RssFeedImage(
        title = title,
        url = url,
        link = link,
        description = description
    )

    private fun LocalRssFeedYoutubeChannel.toRssFeedYoutubeChannel() = RssFeedYoutubeChannel(
        channelId = channelId
    )

}


