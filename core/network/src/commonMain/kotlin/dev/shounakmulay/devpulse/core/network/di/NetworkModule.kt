package dev.shounakmulay.devpulse.core.network.di

import dev.shounakmulay.devpulse.core.network.createHttpClient
import io.ktor.client.HttpClient
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class NetworkModule {
    @Single
    fun httpClient(): HttpClient = createHttpClient()
}
