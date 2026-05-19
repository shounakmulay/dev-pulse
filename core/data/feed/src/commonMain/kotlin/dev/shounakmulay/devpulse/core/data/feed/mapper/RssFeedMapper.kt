package dev.shounakmulay.devpulse.core.data.feed.mapper

import com.prof18.rssparser.model.RssChannel
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeed
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeedImage
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalRssFeedYoutubeChannel
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedImage
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedYoutubeChannel
import org.koin.core.annotation.Factory

@Factory
object RssFeedMapper {

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



