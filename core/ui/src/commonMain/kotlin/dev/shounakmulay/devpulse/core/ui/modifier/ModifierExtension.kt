package dev.shounakmulay.devpulse.core.ui.modifier

import androidx.compose.ui.Modifier

fun Modifier.ifElse(
    condition: Boolean,
    ifTrue: Modifier.() -> Modifier,
    ifFalse: Modifier.() -> Modifier = { this }
): Modifier = if (condition) {
    then(ifTrue(Modifier))
} else {
    then(ifFalse(Modifier))
}
