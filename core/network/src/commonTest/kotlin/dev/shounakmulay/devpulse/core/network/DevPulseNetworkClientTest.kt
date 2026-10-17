package dev.shounakmulay.devpulse.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DevPulseNetworkClientTest {
    @Test
    fun `Given mixed case response header names When reading headers Then lookup ignores case and preserves values`() = runTest {
        listOf("etag", "ETAG", "eTaG", "ETag").forEach { serverHeaderName ->
            val engine = MockEngine {
                respond(
                    content = "{}",
                    status = HttpStatusCode.OK,
                    headers = headersOf(serverHeaderName, "W/\"AbC123\"")
                )
            }
            val httpClient = HttpClient(engine)
            try {
                val networkClient: DevPulseNetworkClient = KtorDevPulseNetworkClient(httpClient)
                val response = networkClient.get("https://example.com/master.json")

                listOf("etag", "ETAG", "eTaG", "ETag").forEach { lookupName ->
                    assertEquals(
                        expected = "W/\"AbC123\"",
                        actual = response.header(lookupName),
                        message = "Server header $serverHeaderName, lookup $lookupName"
                    )
                }
                assertNull(response.header("X-Missing"))
            } finally {
                httpClient.close()
            }
        }
    }

    @Test
    fun `Given configured content negotiation When decoding a generic body Then Ktor uses the configured serializer`() = runTest {
        val engine = MockEngine { request ->
            assertEquals("custom-value", request.headers["X-Test"])
            respond(
                content = """[{"name":"DevPulse","extra":true}]""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val httpClient = HttpClient(engine) {
            install(ContentNegotiation) {
                json(kotlinx.serialization.json.Json { ignoreUnknownKeys = true })
            }
        }
        try {
            val networkClient: DevPulseNetworkClient = KtorDevPulseNetworkClient(httpClient)

            val result = networkClient.get(
                url = "https://example.com/feeds",
                headers = mapOf("X-Test" to "custom-value")
            ).body<List<TestFeed>>()

            assertEquals(listOf(TestFeed(name = "DevPulse")), result)

            val directResult = networkClient.get<List<TestFeed>>(
                url = "https://example.com/feeds",
                headers = mapOf("X-Test" to "custom-value")
            )

            assertEquals(listOf(TestFeed(name = "DevPulse")), directResult)
        } finally {
            httpClient.close()
        }
    }

    @Serializable
    internal data class TestFeed(val name: String)

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
