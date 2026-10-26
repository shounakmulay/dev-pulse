package dev.shounakmulay.devpulse.core.common.startup

interface StartupAsyncInitialiser {
    val priority: StartupPriority
    suspend fun initialise()
}

enum class StartupPriority(val priority: Int) {
    LOWEST(Int.MIN_VALUE),
    LOW(100),
    MEDIUM(200),
    HIGH(300),
    HIGHEST(Int.MAX_VALUE)
}
