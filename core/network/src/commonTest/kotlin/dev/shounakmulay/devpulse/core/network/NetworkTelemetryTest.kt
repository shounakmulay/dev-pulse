package dev.shounakmulay.devpulse.core.network

import dev.shounakmulay.devpulse.core.telemetry.api.*
import dev.shounakmulay.devpulse.core.telemetry.runtime.RecordingTelemetryProvider
import dev.shounakmulay.devpulse.core.telemetry.runtime.TelemetryRuntime
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class NetworkTelemetryTest {
    @Test
    fun `Given two providers and private feed data When fetching Then safe equivalent diagnostics preserve the response`() = runTest {
        val first = RecordingTelemetryProvider(descriptor("first"))
        val second = RecordingTelemetryProvider(descriptor("second"))
        val telemetry = TelemetryRuntime(
            release = AppReleaseContext("app", "jvm", "1", "1", "test", "test"),
            providers = listOf(first, second),
            dispatcher = StandardTestDispatcher(testScheduler),
        )
        telemetry.applyConsent(TelemetryConsent(diagnostics = true))
        val client = HttpClient(MockEngine { request ->
            assertEquals("private", request.url.parameters["search"])
            assertEquals("session=private", request.headers["Cookie"])
            assertNull(request.headers["traceparent"])
            assertNull(request.headers["baggage"])
            respond("private article content", HttpStatusCode.OK)
        })
        try {
            val network = KtorDevPulseNetworkClient(client, telemetry, telemetry)
            val body = telemetry.trace(OperationDefinition("fixture.load")) {
                network.get("https://private.example/feed?search=private", mapOf("Cookie" to "session=private")).bodyAsText()
            }
            assertEquals("private article content", body)
            assertTrue(telemetry.flush(1.seconds).completed)
            assertEquals(first.records(), second.records())
            val spans = first.records().filterIsInstance<CanonicalRecord.Span>()
            assertEquals(setOf("network.http_get", "network.body_text", "fixture.load"), spans.map { it.name }.toSet())
            assertTrue(spans.all { it.attributes.values.isEmpty() })
            val counts = first.records().filterIsInstance<CanonicalRecord.Counter>()
            assertEquals(listOf("network.requests"), counts.map { it.name })
            assertEquals(mapOf("outcome" to "success"), counts.single().attributes.values)
            assertTrue(first.records().filterIsInstance<CanonicalRecord.Error>().isEmpty())
            assertTrue(first.records().filterIsInstance<CanonicalRecord.Event>().isEmpty())
        } finally {
            client.close()
            telemetry.close(1.seconds)
        }
    }

    @Test
    fun `Given denied consent When fetching Then request succeeds without diagnostics`() = runTest {
        val provider = RecordingTelemetryProvider()
        val telemetry = TelemetryRuntime(
            release = AppReleaseContext("app", "jvm", "1", "1", "test", "test"),
            providers = listOf(provider),
            dispatcher = StandardTestDispatcher(testScheduler),
        )
        val client = HttpClient(MockEngine { respond("ok", HttpStatusCode.OK) })
        try {
            assertEquals("ok", KtorDevPulseNetworkClient(client, telemetry, telemetry).get("https://example.com").bodyAsText())
            assertTrue(telemetry.flush(1.seconds).completed)
            assertTrue(provider.records().isEmpty())
        } finally {
            client.close()
            telemetry.close(1.seconds)
        }
    }

    private fun descriptor(id: String) = ProviderDescriptor(
        id = id,
        capabilities = TelemetryCapability.entries.toSet(),
        supportedPlatforms = setOf("jvm"),
        adapterVersion = "test",
    )
}
