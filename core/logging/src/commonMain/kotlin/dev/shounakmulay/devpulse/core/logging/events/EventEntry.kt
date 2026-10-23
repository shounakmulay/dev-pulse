package dev.shounakmulay.devpulse.core.logging.events

data class EventEntry(
    val name: String,
    val data: Map<String, String> = emptyMap(),
)
