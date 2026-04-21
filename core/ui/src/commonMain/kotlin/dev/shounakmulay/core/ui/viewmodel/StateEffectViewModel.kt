package dev.shounakmulay.core.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import dev.shounakmulay.core.ui.effect.Effect
import dev.shounakmulay.core.ui.screen.ScreenState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import kotlin.contracts.ExperimentalContracts

abstract class StateEffectViewModel<STATE : ScreenState> : ViewModel(), KoinComponent {

    private val _state = MutableStateFlow(createInitialState())
    val state: StateFlow<STATE> = _state

    private val _effect = Channel<Effect>()
    val effect: Flow<Effect> =
        _effect.consumeAsFlow().shareIn(viewModelScope, SharingStarted.Lazily)

    protected fun setState(block: STATE.() -> STATE) {
        _state.update(block)
    }

    protected suspend fun <T> withState(block: suspend STATE.() -> T): T = block(state.value)

    @OptIn(ExperimentalContracts::class)
    protected suspend fun setEffect(effect: Effect) {
        _effect.send(effect)
    }

    abstract fun createInitialState(): STATE

    final fun unhandledEffect(effect: Effect): Unit = throw IllegalStateException(
        "Effect $effect is not handled by ViewModel $this.",
    )

    @Composable
    fun collectAsState() = state.collectAsStateWithLifecycle()
}