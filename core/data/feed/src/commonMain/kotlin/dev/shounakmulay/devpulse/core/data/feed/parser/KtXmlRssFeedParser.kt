package dev.shounakmulay.devpulse.core.data.feed.parser

import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeed
import dev.shounakmulay.devpulse.core.network.DevPulseNetworkClient
import dev.shounakmulay.devpulse.core.network.bodyAsText

internal class KtXmlRssFeedParser(
    private val networkClient: DevPulseNetworkClient,
    private val xmlParser: KtXmlFeedParser
) : FeedParser<ParsedFeed> {
    override suspend fun parseFeed(url: String): ParsedFeed {
        val xml = networkClient.get(url).bodyAsText()
        return xmlParser.parseText(sourceUrl = url, xml = xml)
    }
}
