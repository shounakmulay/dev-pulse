package dev.shounakmulay.devpulse.core.ui.event

interface EventHandler<in T: ScreenEvent> {
    fun onEvent(event: T)
}