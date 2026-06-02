package dev.shounakmulay.devpulse.core.data.feed.parser

import com.prof18.rssparser.RssParser
import com.prof18.rssparser.model.RssChannel
import dev.shounakmulay.devpulse.core.network.DevPulseNetworkClient
import dev.shounakmulay.devpulse.core.network.bodyAsText

internal class Prof18RssFeedParser(
    private val networkClient: DevPulseNetworkClient,
    private val rssParser: RssParser
) : FeedParser<RssChannel> {
    override suspend fun parseFeed(url: String): RssChannel {
        val xml = networkClient.get(url).bodyAsText()
        return rssParser.parse(xml)
    }
}
