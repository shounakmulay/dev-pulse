package dev.shounakmulay.devpulse.core.domain.feed.feed

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCatchingOnDefault
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedRepository
import dev.shounakmulay.devpulse.core.domain.models.feed.OpmlFeedImportData
import org.koin.core.annotation.Factory

@Factory
class ExtractOpmlFeedsUseCase(
    private val feedRepository: FeedRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend fun fromXml(opml: String): Result<List<OpmlFeedImportData>> =
        dispatcherProvider.runCatchingOnDefault {
            feedRepository.extractOpmlFeeds(opml)
        }

    suspend fun fromUrl(url: String): Result<List<OpmlFeedImportData>> =
        dispatcherProvider.runCatchingOnDefault {
            feedRepository.extractOpmlFeedsFromUrl(url)
        }
}
