package dev.shounakmulay.devpulse.core.network

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import org.koin.core.annotation.Factory

@Factory
internal class KtorDevPulseNetworkClient(
    private val httpClient: HttpClient
) : DevPulseNetworkClient {
    override suspend fun get(url: String, headers: Map<String, String>): DevPulseNetworkResponse {
        val response = httpClient.get(url) {
            headers.forEach { (key, value) ->
                header(key, value)
            }
        }
        return DevPulseNetworkResponse { response.bodyAsText() }
    }
}