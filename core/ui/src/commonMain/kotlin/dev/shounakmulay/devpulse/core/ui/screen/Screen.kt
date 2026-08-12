package dev.shounakmulay.devpulse.core.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.ui.effect.Effect
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
inline fun <STATE : ScreenState, EFFECT : Effect, reified VM : MviViewModel<STATE, EFFECT>> Screen(
    viewModel: VM,
    modifier: Modifier = Modifier,
    crossinline onEffect: suspend (Effect) -> Unit,
    noinline topAppBar: (@Composable STATE.() -> Unit)? = null,
    noinline bottomBar: (@Composable STATE.() -> Unit)? = null,
    noinline floatingActionButton: (@Composable STATE.() -> Unit)? = null,
    crossinline content: @Composable BoxScope.(STATE) -> Unit
) {
    val state by viewModel.collectAsState()

    viewModel.collectSideEffect {
        onEffect(it)
    }



    Scaffold(
        modifier = modifier,
        topBar = {
            if (topAppBar != null) {
                topAppBar(state)
            }
        },
        bottomBar = {
            if (bottomBar != null) {
                bottomBar(state)
            }
        },
        floatingActionButton = {
            if (floatingActionButton != null) {
                floatingActionButton(state)
            }
        }
    ) {
        Box(Modifier.padding(it).fillMaxSize()) {
            content(state)
        }
    }
}
