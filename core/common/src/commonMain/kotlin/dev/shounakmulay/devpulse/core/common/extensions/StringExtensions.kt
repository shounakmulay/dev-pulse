package dev.shounakmulay.devpulse.core.common.extensions

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
inline fun String?.ifNullOrBlank(block: () -> String): String {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return orEmpty().ifBlank { block() }
}