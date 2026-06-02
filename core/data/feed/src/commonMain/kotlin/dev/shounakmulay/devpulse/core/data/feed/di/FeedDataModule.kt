package dev.shounakmulay.devpulse.core.data.feed.di

import com.prof18.rssparser.RssParser
import dev.shounakmulay.devpulse.core.data.feed.parser.KtXmlFeedParser
import dev.shounakmulay.devpulse.core.data.feed.parser.KtXmlRssFeedParser
import dev.shounakmulay.devpulse.core.data.feed.parser.Prof18RssFeedParser
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedImportFallbackParser
import dev.shounakmulay.devpulse.core.data.feed.repository.KtXmlFeedImportCandidate
import dev.shounakmulay.devpulse.core.data.feed.repository.Prof18FeedImportCandidate
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
    internal fun provideProf18RssFeedParser(
        networkClient: DevPulseNetworkClient,
        rssParser: RssParser
    ): Prof18RssFeedParser {
        return Prof18RssFeedParser(
            networkClient = networkClient,
            rssParser = rssParser
        )
    }

    @Factory
    internal fun provideKtXmlRssFeedParser(
        networkClient: DevPulseNetworkClient,
        xmlParser: KtXmlFeedParser
    ): KtXmlRssFeedParser {
        return KtXmlRssFeedParser(
            networkClient = networkClient,
            xmlParser = xmlParser
        )
    }

    @Factory
    internal fun provideFeedImportFallbackParser(
        prof18Candidate: Prof18FeedImportCandidate,
        ktXmlCandidate: KtXmlFeedImportCandidate,
        logger: DPLogger
    ): FeedImportFallbackParser {
        return FeedImportFallbackParser(
            candidates = listOf(prof18Candidate, ktXmlCandidate),
            logger = logger
        )
    }
}
