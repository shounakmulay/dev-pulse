package dev.shounakmulay.devpulse.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DevPulseNetworkClientTest {
    @Test
    fun `Given Ktor client response When body text is read Then response text is returned`() = runTest {
        val engine = MockEngine { request ->
            assertEquals("https://example.com/feed.xml", request.url.toString())
            respond(
                content = "feed text",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "text/plain")
            )
        }
        val httpClient = HttpClient(engine)
        val networkClient = KtorDevPulseNetworkClient(httpClient)

        val result = networkClient.get("https://example.com/feed.xml").bodyAsText()

        assertEquals("feed text", result)
        httpClient.close()
    }
}
