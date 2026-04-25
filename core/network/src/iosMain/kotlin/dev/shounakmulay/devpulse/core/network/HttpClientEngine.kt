package dev.shounakmulay.devpulse.core.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

internal actual fun createEngine(): HttpClientEngine = Darwin.create()
