package dev.shounakmulay.devpulse.core.domain.feed

import org.koin.core.annotation.Factory

@Factory
class NormalizeUrlUseCase {
    operator fun invoke(url: String): String {
        val value = url.trim()

        val normalised = when {
            value.startsWith("https://") -> value
            value.startsWith("http://") -> value.replace("http://", "https://")
            value.startsWith("//") -> "https:$value"
            else -> "https://$value"
        }

        return normalised
    }
}