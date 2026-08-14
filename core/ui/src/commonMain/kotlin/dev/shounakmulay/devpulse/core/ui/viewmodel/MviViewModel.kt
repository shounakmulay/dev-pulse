package dev.shounakmulay.devpulse.core.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dev.shounakmulay.devpulse.core.ui.effect.Effect
import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.KSerializer
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.orbitmvi.orbit.OrbitContainerHost
import org.orbitmvi.orbit.syntax.Syntax
import org.orbitmvi.orbit.viewmodel.orbitContainer

abstract class MviViewModel<STATE : ScreenState, EFFECT : Effect>(initialState: STATE) :
    OrbitContainerHost<STATE, STATE, EFFECT>, ViewModel(), KoinComponent {
    private val savedStateHandle: SavedStateHandle by inject()
    override val container = orbitContainer<STATE, EFFECT>(
        initialState = initialState,
        savedStateHandle = savedStateHandle,
        serializer = createStateSerializer(),
    ) {
        repeatOnSubscription {
            bindStateSources(this)
        }
    }

    val state: StateFlow<STATE> = container.stateFlow

    protected fun setState(block: STATE.() -> STATE) = intent {
        reduce {
            state.block()
        }
    }

    protected suspend fun Syntax<STATE, EFFECT>.setState(block: STATE.() -> STATE) {
        reduce { state.block() }
    }

    protected fun postEffect(effect: EFFECT) = intent {
        postSideEffect(effect)
    }

    abstract fun createStateSerializer(): KSerializer<STATE>

    protected open fun bindStateSources(stateSubscriptionScope: CoroutineScope) {}

    final fun unhandledEffect(effect: Effect): Unit = throw IllegalStateException(
        "Effect $effect is not handled by ViewModel $this.",
    )
}