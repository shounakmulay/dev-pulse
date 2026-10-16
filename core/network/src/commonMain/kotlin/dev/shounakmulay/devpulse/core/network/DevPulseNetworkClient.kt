package dev.shounakmulay.devpulse.core.network

import io.ktor.http.HttpHeaders
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

interface DevPulseNetworkClient {
    suspend fun get(url: String, headers: Map<String, String> = emptyMap()): DevPulseNetworkResponse
    suspend fun <T> get(
        url: String,
        headers: Map<String, String> = emptyMap(),
        type: KClass<*>,
        kotlinType: KType
    ): T = get(url = url, headers = headers).body(type = type, kotlinType = kotlinType)
}

suspend inline fun <reified T> DevPulseNetworkClient.get(
    url: String,
    headers: Map<String, String> = emptyMap()
): T = get(url = url, headers = headers, type = T::class, kotlinType = typeOf<T>())
