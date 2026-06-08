package dev.shounakmulay.devpulse.core.data.feed.parser.xml

import com.prof18.rssparser.model.RawEnclosure
import com.prof18.rssparser.model.RawMediaContent
import com.prof18.rssparser.model.RssChannel
import com.prof18.rssparser.model.RssImage
import com.prof18.rssparser.model.RssItem
import com.prof18.rssparser.model.YoutubeChannelData
import com.prof18.rssparser.model.YoutubeItemData
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeed
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedImage
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedItem
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedItemMediaContent
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedItemRawEnclosure
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedItemYoutubeData
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedMetadata
import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeedYoutubeChannel
import kotlinx.coroutines.flow.asFlow
import org.koin.core.annotation.Factory

@Factory
internal class Prof18ParsedFeedMapper {
    fun toParsedFeed(from: RssChannel): ParsedFeed {
        return ParsedFeed(
            metadata = toMetadata(from),
            items = from.items.mapIndexed { index, item ->
                toItem(from = item)
            }.asFlow(),
            issues = emptyList()
        )
    }

    private fun toMetadata(from: RssChannel): ParsedFeedMetadata {
        return ParsedFeedMetadata(
            title = from.title,
            link = from.link,
            description = from.description,
            image = from.image?.toParsedImage(),
            lastBuildDate = from.lastBuildDate,
            updatePeriod = from.updatePeriod,
            youtubeChannel = from.youtubeChannelData?.toParsedYoutubeChannel()
        )
    }

    private fun toItem(from: RssItem): ParsedFeedItem {
        return ParsedFeedItem(
            guid = from.guid,
            title = from.title,
            author = from.author,
            link = from.link,
            pubDate = from.pubDate,
            description = from.description,
            content = from.content,
            image = from.image,
            audio = from.audio,
            video = from.video,
            sourceName = from.sourceName,
            sourceUrl = from.sourceUrl,
            categories = from.categories,
            commentsUrl = from.commentsUrl,
            youtubeItemData = from.youtubeItemData?.toParsedYoutubeItemData(),
            rawEnclosure = from.rawEnclosure?.toParsedRawEnclosure(),
            rawMediaContent = from.rawMediaContent?.toParsedMediaContent()
        )
    }

    private fun RssImage.toParsedImage(): ParsedFeedImage {
        return ParsedFeedImage(
            title = title,
            url = url,
            link = link,
            description = description
        )
    }

    private fun YoutubeChannelData.toParsedYoutubeChannel(): ParsedFeedYoutubeChannel {
        return ParsedFeedYoutubeChannel(
            channelId = channelId
        )
    }

    private fun YoutubeItemData.toParsedYoutubeItemData(): ParsedFeedItemYoutubeData {
        return ParsedFeedItemYoutubeData(
            videoId = videoId,
            title = title,
            videoUrl = videoUrl,
            thumbnailUrl = thumbnailUrl,
            description = description,
            viewsCount = viewsCount,
            likesCount = likesCount
        )
    }

    private fun RawEnclosure.toParsedRawEnclosure(): ParsedFeedItemRawEnclosure {
        return ParsedFeedItemRawEnclosure(
            url = url,
            length = length,
            type = type
        )
    }

    private fun RawMediaContent.toParsedMediaContent(): ParsedFeedItemMediaContent {
        return ParsedFeedItemMediaContent(
            url = url,
            type = type,
            medium = medium
        )
    }
}
