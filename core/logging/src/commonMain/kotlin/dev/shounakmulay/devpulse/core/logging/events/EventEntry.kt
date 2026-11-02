package dev.shounakmulay.devpulse.core.logging.events

data class EventEntry(
    val name: DPEvents,
    val data: Map<DPEventKey, String> = emptyMap(),
)
