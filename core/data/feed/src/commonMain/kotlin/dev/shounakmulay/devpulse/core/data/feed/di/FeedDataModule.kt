package dev.shounakmulay.devpulse.core.data.feed.di

import com.prof18.rssparser.RssParser
import dev.shounakmulay.devpulse.core.data.feed.parser.xml.KtXmlRssFeedParser
import dev.shounakmulay.devpulse.core.data.feed.parser.xml.Prof18RssFeedParser
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedImportFallbackParser
import dev.shounakmulay.devpulse.core.logging.DPLogger
import dev.shounakmulay.devpulse.core.network.DevPulseNetworkClient
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module
@ComponentScan("dev.shounakmulay.devpulse.core.data.feed")
class FeedDataModule {
    @Factory
    fun provideRssParser(): RssParser = RssParser()

    @Factory
    internal fun provideFeedImportFallbackParser(
        prof18RssFeedParser: Prof18RssFeedParser,
        ktXmlRssFeedParser: KtXmlRssFeedParser,
        networkClient: DevPulseNetworkClient,
        logger: DPLogger
    ): FeedImportFallbackParser {
        return FeedImportFallbackParser(
            primaryParser = ktXmlRssFeedParser,
            secondaryParser = prof18RssFeedParser,
            networkClient = networkClient,
            logger = logger
        )
    }
}
