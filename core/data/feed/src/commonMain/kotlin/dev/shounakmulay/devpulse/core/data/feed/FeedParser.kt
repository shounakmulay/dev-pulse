package dev.shounakmulay.devpulse.core.data.feed

import com.prof18.rssparser.RssParser
import com.prof18.rssparser.model.RawMediaContent
import com.prof18.rssparser.model.RssChannel
import com.prof18.rssparser.model.RssItem

internal interface FeedParser<out T> {
    suspend fun parseFeed(url: String): T
}

class ContentParser(
    private val rssParser: RssParser
) : FeedParser<RssChannel> {
    override suspend fun parseFeed(url: String): RssChannel {
        return rssParser.getRssChannel(url)
    }

}

data class LocalRssFeedItemRawEnclosure(
    val url: String?,
    val length: Long?,
    val type: String?,
)

data class LocalRssFeedItemRawMediaContent(
    val url: String?,
    val type: String?,
    val medium: String?,
)

data class LocalRssFeedItem(
    val guid: String?,
    val title: String?,
    val author: String?,
    val link: String?,
    val pubDate: String?,
    val description: String?,
    val content: String?,
    val image: String?,
    val audio: String?,
    val video: String?,
    val sourceName: String?,
    val sourceUrl: String?,
    val categories: List<String>,
    val commentsUrl: String?,
    val youtubeItemData: LocalRssFeedItemYoutubeData?,
    val rawEnclosure: LocalRssFeedItemRawEnclosure?,
    val rawMediaContent: RawMediaContent? = null,
)

data class LocalRssFeedItemYoutubeData(
    val videoId: String?,
    val title: String?,
    val videoUrl: String?,
    val thumbnailUrl: String?,
    val description: String?,
    val viewsCount: Int?,
    val likesCount: Int?,
)

data class LocalRssFeed(
    val id: Int,
    val title: String?,
    val link: String?,
    val description: String?,
    val image: LocalRssFeedImage?,
    val lastBuildDate: String?,
    val updatePeriod: String?,
    val items: List<RssItem>,
    val youtubeChannel: LocalRssFeedYoutubeChannel?,
    val createdAt: Long,
    val updatedAt: Long
)

data class LocalRssFeedYoutubeChannel(
    val channelId: String?
)

data class LocalRssFeedImage(
    val title: String?,
    val url: String?,
    val link: String?,
    val description: String?
)
