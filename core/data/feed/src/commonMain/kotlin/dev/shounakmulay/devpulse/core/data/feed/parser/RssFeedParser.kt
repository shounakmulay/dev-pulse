package dev.shounakmulay.devpulse.core.data.feed.parser

import dev.shounakmulay.devpulse.core.data.feed.parser.model.ParsedFeed
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import org.koin.core.annotation.Factory

@Factory(binds = [FeedParser::class])
internal class RssFeedParser(
    private val httpClient: HttpClient,
    private val xmlParser: KtXmlFeedParser
) : FeedParser {
    override suspend fun parseFeed(url: String): ParsedFeed {
        val xml = httpClient.get(url).bodyAsText()
        return parseText(sourceUrl = url, xml = xml)
    }

    override fun parseText(sourceUrl: String?, xml: String): ParsedFeed {
        return xmlParser.parseText(sourceUrl = sourceUrl, xml = xml)
    }
}
