package dev.shounakmulay.devpulse.core.network.di

import dev.shounakmulay.devpulse.core.network.createHttpClient
import dev.shounakmulay.devpulse.core.logging.DPLogger
import io.ktor.client.HttpClient
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
@ComponentScan("dev.shounakmulay.devpulse.core.network")
class NetworkModule

@Singleton
fun httpClient(logger: DPLogger): HttpClient =
    createHttpClient(logger.withTag("DevPulseHttpClient"))
