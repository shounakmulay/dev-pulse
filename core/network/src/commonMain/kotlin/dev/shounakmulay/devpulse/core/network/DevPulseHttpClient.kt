package dev.shounakmulay.devpulse.core.network

import dev.shounakmulay.devpulse.core.logging.DPLogger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal fun createHttpClient(dpLogger: DPLogger): HttpClient = HttpClient(createEngine()) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
        })
    }

    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                dpLogger.d { message }
            }
        }
        level = LogLevel.ALL
        sanitizeHeader { header ->
            header == HttpHeaders.Authorization || header == HttpHeaders.Cookie
        }
    }

    install(HttpCache)
}
