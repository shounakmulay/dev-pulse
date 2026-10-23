package dev.shounakmulay.devpulse.core.logging.events

interface EventSink {
    fun isTrackable(entry: EventEntry): Boolean

    fun track(entry: EventEntry)
}
