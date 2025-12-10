package com.uriolus.core.common.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Base ViewModel for MVI pattern
 * @param S State type
 * @param I Intent type (user actions)
 * @param E Event type (one-time side effects)
 */
abstract class MviViewModel<S : Any, I : Any, E : Any>(
    initialState: S
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _event = Channel<E>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    /**
     * Handle user intents
     */
    abstract fun handleIntent(intent: I)

    /**
     * Update state
     */
    protected fun updateState(reducer: S.() -> S) {
        _state.value = _state.value.reducer()
    }

    /**
     * Send one-time event
     */
    protected fun sendEvent(event: E) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
}
