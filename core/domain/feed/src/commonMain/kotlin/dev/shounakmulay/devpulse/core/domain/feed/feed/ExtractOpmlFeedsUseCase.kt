package dev.shounakmulay.devpulse.core.domain.feed.feed

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCatchingOnDefault
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedRepository
import dev.shounakmulay.devpulse.core.domain.models.feed.OpmlFeedImportData
import dev.shounakmulay.devpulse.core.logging.events.DPEvent
import dev.shounakmulay.devpulse.core.logging.events.DPEvents
import org.koin.core.annotation.Factory

@Factory
class ExtractOpmlFeedsUseCase(
    private val feedRepository: FeedRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val dpEvent: DPEvent,
) {
    suspend fun fromXml(opml: String): Result<List<OpmlFeedImportData>> = dispatcherProvider.runCatchingOnDefault {
        feedRepository.extractOpmlFeeds(opml)
    }.onSuccess {
        dpEvent.track(DPEvents.OPML_EXTRACTION_COMPLETED, data = mapOf("source" to "xml"))
    }.onFailure {
        dpEvent.track(DPEvents.OPML_EXTRACTION_FAILED, data = mapOf("source" to "xml"))
    }

    suspend fun fromUrl(url: String): Result<List<OpmlFeedImportData>> = dispatcherProvider.runCatchingOnDefault {
        feedRepository.extractOpmlFeedsFromUrl(url)
    }.onSuccess {
        dpEvent.track(DPEvents.OPML_EXTRACTION_COMPLETED, data = mapOf("source" to "url"))
    }.onFailure {
        dpEvent.track(DPEvents.OPML_EXTRACTION_FAILED, data = mapOf("source" to "url"))
    }
}
