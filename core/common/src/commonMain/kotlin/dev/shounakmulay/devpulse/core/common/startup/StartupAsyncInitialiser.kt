package dev.shounakmulay.devpulse.core.common.startup

interface StartupAsyncInitialiser {
    suspend fun initialise()
}