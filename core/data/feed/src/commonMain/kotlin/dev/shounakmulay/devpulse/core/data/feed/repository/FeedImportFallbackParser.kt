package dev.shounakmulay.devpulse.core.data.feed.repository

import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.logging.DPLogger
import dev.shounakmulay.devpulse.core.network.DevPulseNetworkClient
import dev.shounakmulay.devpulse.core.network.bodyAsText
import org.koin.core.annotation.Factory

@Factory
internal class FeedImportFallbackParser(
    private val primaryParser: FeedImportCandidate,
    private val secondaryParser: FeedImportCandidate,
    private val networkClient: DevPulseNetworkClient,
    logger: DPLogger,
) {
    private val logger = logger.withTag(Tag)

    suspend fun import(entry: RssFeedQueueEntry) {
        val xml = networkClient.get(entry.url).bodyAsText()
        var lastFailure: Throwable? = null

        val primaryParserResult = import(
            entry = entry,
            xml = xml,
            candidate = primaryParser
        ).onFailure {
            lastFailure = it
        }

        if (primaryParserResult.isSuccess) {
            return
        }

        import(
            entry = entry,
            xml = xml,
            candidate = secondaryParser
        ).onSuccess {
            return
        }.onFailure {
            lastFailure = it
        }

        throw checkNotNull(lastFailure)
    }

    private suspend fun import(
        entry: RssFeedQueueEntry,
        xml: String,
        candidate: FeedImportCandidate
    ): Result<Unit> = runCatching {
        logger.d {
            "RSS import candidate started candidate=${candidate.id} queueId=${entry.id} source=${entry.url.sourceSummary()}"
        }
        candidate.import(entry = entry, xml = xml)
    }.logImportResult(entry = entry, candidate = candidate)

    private fun Result<Unit>.logImportResult(
        entry: RssFeedQueueEntry,
        candidate: FeedImportCandidate
    ): Result<Unit> {
        return this.onSuccess {
            logger.d {
                "RSS import candidate succeeded candidate=${candidate.id} queueId=${entry.id} source=${entry.url.sourceSummary()}"
            }
        }.onFailure {
            logger.e(it) {
                "RSS import candidate failed candidate=${candidate.id} queueId=${entry.id} source=${entry.url.sourceSummary()}"
            }
        }
    }

    private fun String.sourceSummary(): String {
        val withoutScheme = substringAfter("://", this)
        val host =
            withoutScheme.substringBefore('/').substringBefore('?').takeIf { it.isNotBlank() }
        return "host=${host ?: take(80)}"
    }

    private companion object {
        const val Tag = "FeedImportFallbackParser"
    }
}
