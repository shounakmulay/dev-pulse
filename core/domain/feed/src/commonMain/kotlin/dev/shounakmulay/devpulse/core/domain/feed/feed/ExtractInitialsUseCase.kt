package dev.shounakmulay.devpulse.core.domain.feed.feed

import org.koin.core.annotation.Factory

@Factory
class ExtractInitialsUseCase {
    operator fun invoke(str: String): String {
        return str.trim()
            .split(Regex("\\s+"))
            .take(2)
            .mapNotNull { it.firstOrNull() }
            .joinToString("")
            .uppercase()
    }
}