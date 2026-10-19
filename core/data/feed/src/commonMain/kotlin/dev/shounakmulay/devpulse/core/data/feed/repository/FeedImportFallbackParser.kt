package dev.shounakmulay.devpulse.core.data.feed.repository

import dev.shounakmulay.devpulse.core.telemetry.api.*
import kotlin.time.TimeSource
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeed
import dev.shounakmulay.devpulse.core.logging.DPLogger
import dev.shounakmulay.devpulse.core.network.DevPulseNetworkClient
import kotlinx.coroutines.CancellationException
import org.koin.core.annotation.Factory

@Factory
internal class FeedImportFallbackParser(
    private val primaryParser: FeedImportCandidate,
    private val secondaryParser: FeedImportCandidate,
    private val networkClient: DevPulseNetworkClient,
    logger: DPLogger,
    private val tracer: Tracer = NoOpTelemetry,
    private val metrics: Metrics = NoOpTelemetry,
) {
    private val logger = logger.withTag(Tag)

    suspend fun import(entry: RssFeedQueueEntry): ParsedFeed {
        val xml = networkClient.get(entry.url).bodyAsText()
        var lastFailure: Throwable? = null

        for ((index, parser) in listOf(primaryParser, secondaryParser).withIndex()) {
            import(
                entry = entry,
                xml = xml,
                candidate = parser
            ).onSuccess {
                if (index > 0) metrics.increment(FallbackUsed)
                return it
            }.onFailure {
                lastFailure = it
            }
        }

        throw checkNotNull(lastFailure)
    }

    private suspend fun import(
        entry: RssFeedQueueEntry,
        xml: String,
        candidate: FeedImportCandidate
    ): Result<ParsedFeed> = try {
        logger.d {
            "RSS import candidate started candidate=${candidate.id} queueId=${entry.id} " +
                    "source=${entry.url.sourceSummary()}"
        }
        Result.success(tracer.trace(OperationDefinition("feed.parse_attempt")) {
            candidate.parse(entry = entry, xml = xml)
        })
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (failure: Exception) {
        Result.failure(failure)
    }.logImportResult(entry = entry, candidate = candidate)

    private fun Result<ParsedFeed>.logImportResult(
        entry: RssFeedQueueEntry,
        candidate: FeedImportCandidate
    ): Result<ParsedFeed> {
        return this.onSuccess {
            logger.d {
                "RSS import candidate succeeded candidate=${candidate.id} queueId=${entry.id} " +
                        "source=${entry.url.sourceSummary()}"
            }
        }.onFailure {
            logger.e(it) {
                "RSS import candidate failed candidate=${candidate.id} queueId=${entry.id} " +
                        "source=${entry.url.sourceSummary()}"
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
        val FallbackUsed = CounterDefinition("feed.parser_fallback_used")
        const val Tag = "FeedImportFallbackParser"
    }
}
