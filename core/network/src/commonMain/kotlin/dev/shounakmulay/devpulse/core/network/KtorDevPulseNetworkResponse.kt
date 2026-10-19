package dev.shounakmulay.devpulse.core.network

import dev.shounakmulay.devpulse.core.telemetry.api.NoOpTelemetry
import dev.shounakmulay.devpulse.core.telemetry.api.OperationDefinition
import dev.shounakmulay.devpulse.core.telemetry.api.Tracer
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.util.reflect.TypeInfo
import kotlin.reflect.KClass
import kotlin.reflect.KType

internal class KtorDevPulseNetworkResponse(
    private val response: HttpResponse,
    override val status: Int = response.status.value,
    private val tracer: Tracer = NoOpTelemetry,
) : DevPulseNetworkResponse {
    override fun header(name: String): String? = response.headers[name]

    override suspend fun bodyAsText(): String = tracer.trace(OperationDefinition("network.body_text")) { response.bodyAsText() }

    override suspend fun <T> body(type: KClass<*>, kotlinType: KType): T =
        tracer.trace(OperationDefinition("network.body_decode")) {
            response.body(TypeInfo(type = type, kotlinType = kotlinType))
        }
}
