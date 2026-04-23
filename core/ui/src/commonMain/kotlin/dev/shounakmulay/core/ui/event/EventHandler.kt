package dev.shounakmulay.core.ui.event

interface EventHandler<T: ScreenEvent> {
    fun onEvent(event: T)
}