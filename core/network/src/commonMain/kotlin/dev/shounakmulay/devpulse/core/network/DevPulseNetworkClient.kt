package dev.shounakmulay.devpulse.core.network

interface DevPulseNetworkClient {
    suspend fun get(url: String, headers: Map<String, String> = emptyMap()): DevPulseNetworkResponse

    companion object {
        object HttpHeaders {
            const val USER_AGENT = "User-Agent"
        }

        object HttpUserAgent {
            const val BROWSER = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
        }
    }
}
