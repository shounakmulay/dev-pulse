package dev.shounakmulay.devpulse

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform