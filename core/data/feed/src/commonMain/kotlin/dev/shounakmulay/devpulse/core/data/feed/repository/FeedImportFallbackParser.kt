package dev.shounakmulay.devpulse.core.data.feed.repository

import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.logging.DPLogger

internal class FeedImportFallbackParser(
    private val candidates: List<FeedImportCandidate>,
    logger: DPLogger
) {
    private val logger = logger.withTag(Tag)

    suspend fun import(entry: RssFeedQueueEntry) {
        var lastFailure: Throwable? = null
        candidates.forEach { candidate ->
            logger.d {
                "RSS import candidate started candidate=${candidate.id} queueId=${entry.id} source=${entry.url.sourceSummary()}"
            }
            val result = runCatching {
                candidate.import(entry)
            }
            result.onSuccess {
                logger.d {
                    "RSS import candidate succeeded candidate=${candidate.id} queueId=${entry.id} source=${entry.url.sourceSummary()}"
                }
                return
            }.onFailure { throwable ->
                lastFailure = throwable
                logger.e(throwable) {
                    "RSS import candidate failed candidate=${candidate.id} queueId=${entry.id} source=${entry.url.sourceSummary()}"
                }
            }
        }
        throw checkNotNull(lastFailure)
    }

    private fun String.sourceSummary(): String {
        val withoutScheme = substringAfter("://", this)
        val host = withoutScheme.substringBefore('/').substringBefore('?').takeIf { it.isNotBlank() }
        return "host=${host ?: take(80)}"
    }

    private companion object {
        const val Tag = "FeedImportFallbackParser"
    }
}
