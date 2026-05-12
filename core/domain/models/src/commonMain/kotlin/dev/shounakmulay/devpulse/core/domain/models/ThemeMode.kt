package dev.shounakmulay.devpulse.core.domain.models

enum class ThemeMode {
    LIGHT, DARK, SYSTEM;

    companion object {
        val DEFAULT = SYSTEM
    }
}