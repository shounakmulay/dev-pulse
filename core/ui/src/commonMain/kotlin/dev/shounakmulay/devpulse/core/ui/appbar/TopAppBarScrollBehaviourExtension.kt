package dev.shounakmulay.devpulse.core.ui.appbar

import androidx.compose.material3.TopAppBarScrollBehavior

fun TopAppBarScrollBehavior.expand() {
    state.heightOffset = 0f
}