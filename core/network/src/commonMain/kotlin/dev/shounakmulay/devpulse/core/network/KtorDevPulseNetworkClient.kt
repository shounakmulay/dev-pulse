package dev.shounakmulay.devpulse.core.network

import dev.shounakmulay.devpulse.core.telemetry.api.*
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import kotlinx.coroutines.CancellationException
import org.koin.core.annotation.Factory
import kotlin.time.TimeSource

@Factory
internal class KtorDevPulseNetworkClient(
    private val httpClient: HttpClient,
    private val tracer: Tracer = NoOpTelemetry,
    private val metrics: Metrics = NoOpTelemetry,
) : DevPulseNetworkClient {
    override suspend fun get(url: String, headers: Map<String, String>): DevPulseNetworkResponse {
        val started = TimeSource.Monotonic.markNow()
        var outcome = "failed"
        try {
            return tracer.trace(GetOperation) {
                val response = httpClient.get(url) {
                    headers.forEach { (key, value) -> header(key, value) }
                }
                outcome = when (response.status.value) {
                    in 200..299 -> "success"
                    in 400..499 -> "client_error"
                    in 500..599 -> "server_error"
                    else -> "other"
                }
                KtorDevPulseNetworkResponse(response = response, tracer = tracer)
            }
        } catch (cancelled: CancellationException) {
            outcome = "cancelled"
            throw cancelled
        } finally {
            val dimensions = MetricAttributes.of("outcome" to outcome)
            metrics.increment(Requests, attributes = dimensions)
            metrics.record(Duration, started.elapsedNow().inWholeNanoseconds / 1_000_000.0, dimensions)
        }
    }

    private companion object {
        val GetOperation = OperationDefinition("network.http_get")
        val Outcomes = MetricDimensions(mapOf("outcome" to setOf("success", "client_error", "server_error", "other", "failed", "cancelled")))
        val Requests = CounterDefinition("network.requests", Outcomes)
        val Duration = HistogramDefinition("network.duration", MetricUnit.Milliseconds, Outcomes)
    }
}
