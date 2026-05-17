package dev.shounakmulay.devpulse.core.data.feed.parser

import com.prof18.rssparser.RssParser
import com.prof18.rssparser.model.RssChannel
import org.koin.core.annotation.Factory

@Factory
class RssFeedParser(
    private val rssParser: RssParser
) : FeedParser<RssChannel> {
    override suspend fun parseFeed(url: String): RssChannel {
        val channel = rssParser.getRssChannel(url)

    }
}