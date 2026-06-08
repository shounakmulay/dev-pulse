package dev.shounakmulay.devpulse.core.network

class DevPulseNetworkResponse(
    internal val textProvider: suspend () -> String
)

suspend fun DevPulseNetworkResponse.bodyAsText(): String =
    textProvider()