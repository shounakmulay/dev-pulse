package dev.shounakmulay.devpulse.core.network

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

interface DevPulseNetworkClient {
    suspend fun get(url: String): DevPulseNetworkResponse
}

class DevPulseNetworkResponse(
    internal val textProvider: suspend () -> String
)

suspend fun DevPulseNetworkResponse.bodyAsText(): String =
    textProvider()

internal class KtorDevPulseNetworkClient(
    private val httpClient: HttpClient
) : DevPulseNetworkClient {
    override suspend fun get(url: String): DevPulseNetworkResponse {
        val response = httpClient.get(url)
        return DevPulseNetworkResponse { response.bodyAsText() }
    }
}
