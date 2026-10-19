package dev.shounakmulay.devpulse.core.data.db.paging

import dev.shounakmulay.devpulse.core.data.db.DevPulseDatabase
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedPostQuery
import dev.shounakmulay.devpulse.core.telemetry.api.*
import org.koin.core.annotation.Factory

@Factory
internal class RoomFeedPostPagingSourceProvider(
    private val database: DevPulseDatabase,
    private val tracer: Tracer = NoOpTelemetry,
    private val metrics: Metrics = NoOpTelemetry,
    private val errors: ErrorReporter = NoOpTelemetry,
) : FeedPostPagingSourceProvider {
    override fun getFeedPostPagingSource(query: LocalFeedPostQuery) = LocalCursorPagingSource(
        database = database,
        dataProvider = LocalRssPostWithFeedMetadataPagingDataProvider(
            feedPostDao = database.getFeedContentDao(),
            query = query,
            tracer = tracer,
            metrics = metrics,
        ),
        errors = errors,
    )
}
