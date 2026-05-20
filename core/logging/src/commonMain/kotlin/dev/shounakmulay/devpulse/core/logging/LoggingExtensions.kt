package dev.shounakmulay.devpulse.core.logging

inline fun <reified T : Any> logger(): DPLogger =
    DPLog.tag(T::class.simpleName ?: "Unknown")

fun Any.logger(): DPLogger =
    DPLog.tag(this::class.simpleName ?: UnknownTag)
