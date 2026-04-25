package dev.shounakmulay.core.network

import io.ktor.client.engine.HttpClientEngine

internal expect fun createEngine(): HttpClientEngine