package dev.shounakmulay.core.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import dev.shounakmulay.core.designsystem.components.DPTopAppBar
import dev.shounakmulay.core.ui.effect.Effect
import dev.shounakmulay.core.ui.viewmodel.MviViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

data class ScreenTopAppBarState(
    val title: String,
    val subTitle: String? = null,
    val navigationIcon: ImageVector? = null,
    val onNavigationIconClick: () -> Unit = {}
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
inline fun <STATE : ScreenState, EFFECT : Effect, reified VM : MviViewModel<STATE, EFFECT>> Screen(
    viewModel: VM,
    crossinline onEffect: suspend (Effect) -> Unit,
    crossinline topAppBarStateProvider: STATE.() -> ScreenTopAppBarState? = { null },
    crossinline content: @Composable STATE.() -> Unit
) {
    Screen<STATE, EFFECT, VM>(
        viewModel = viewModel,
        onEffect = onEffect,
        topAppBar = {
            val state by remember(this) {
                derivedStateOf {
                    topAppBarStateProvider(this)
                }
            }
            state?.let {
                DPTopAppBar(
                    title = it.title,
                    subtitle = it.subTitle,

                    )
            }
        },
        content = content
    )
}

@Composable
inline fun <STATE : ScreenState, EFFECT : Effect, reified VM : MviViewModel<STATE, EFFECT>> Screen(
    viewModel: VM,
    crossinline onEffect: suspend (Effect) -> Unit,
    noinline topAppBar: (@Composable STATE.() -> Unit)?,
    crossinline content: @Composable STATE.() -> Unit
) {
    val state by viewModel.collectAsState()

    viewModel.collectSideEffect {
        onEffect(it)
    }

    Scaffold(
        topBar = {
            if (topAppBar != null) {
                topAppBar(state)
            }
        }
    ) {
        Box(Modifier.padding(it)) {
            content(state)
        }
    }
}
