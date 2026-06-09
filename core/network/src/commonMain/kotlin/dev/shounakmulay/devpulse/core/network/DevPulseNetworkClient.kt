package dev.shounakmulay.devpulse.core.network

interface DevPulseNetworkClient {
    suspend fun get(url: String): DevPulseNetworkResponse
}
