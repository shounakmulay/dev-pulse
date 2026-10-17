package dev.shounakmulay.devpulse.core.network

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

interface DevPulseNetworkResponse {
    val status: Int

    fun header(name: String): String?

    suspend fun bodyAsText(): String

    suspend fun <T> body(type: KClass<*>, kotlinType: KType): T
}

suspend inline fun <reified T> DevPulseNetworkResponse.body(): T =
    body(type = T::class, kotlinType = typeOf<T>())
