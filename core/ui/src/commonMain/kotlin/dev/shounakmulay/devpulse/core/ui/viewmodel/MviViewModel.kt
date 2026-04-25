package dev.shounakmulay.devpulse.core.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dev.shounakmulay.devpulse.core.ui.effect.Effect
import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.KSerializer
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.Syntax
import org.orbitmvi.orbit.viewmodel.container

abstract class MviViewModel<STATE : ScreenState, EFFECT : Effect> :
    ContainerHost<STATE, EFFECT>, ViewModel(), KoinComponent {
    private val savedStateHandle: SavedStateHandle by inject()
    override val container = container<STATE, EFFECT>(
        initialState = createInitialState(),
        savedStateHandle = savedStateHandle,
        serializer = createStateSerializer(),
    )
    val state: StateFlow<STATE> = container.stateFlow

    protected fun setState(block: STATE.() -> STATE) = intent {
        reduce {
            state.block()
        }
    }

    protected suspend fun Syntax<STATE, EFFECT>.setState(block: STATE.() -> STATE) {
        reduce { state.block() }
    }

    abstract fun createInitialState(): STATE
    abstract fun createStateSerializer(): KSerializer<STATE>

    final fun unhandledEffect(effect: Effect): Unit = throw IllegalStateException(
        "Effect $effect is not handled by ViewModel $this.",
    )
}