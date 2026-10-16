package dev.shounakmulay.devpulse.core.common.flow

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlin.time.Duration.Companion.milliseconds
import kotlin.jvm.JvmName

@OptIn(FlowPreview::class)
@JvmName("debounceNullableSearch")
fun Flow<String?>.debounceSearch() = debounce {
    if (it.isNullOrBlank()) 0.milliseconds else 300.milliseconds
}

@OptIn(FlowPreview::class)
fun Flow<String>.debounceSearch() = debounce {
    if (it.isBlank()) 0.milliseconds else 300.milliseconds
}
