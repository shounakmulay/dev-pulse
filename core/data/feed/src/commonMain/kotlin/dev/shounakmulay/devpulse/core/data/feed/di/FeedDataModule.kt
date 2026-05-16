package dev.shounakmulay.devpulse.core.data.feed.di

import com.prof18.rssparser.RssParser
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module
@ComponentScan("dev.shounakmulay.devpulse.core.data.feed")
class FeedDataModule {

    @Factory
    fun provideRssParser(): RssParser = RssParser()
}
