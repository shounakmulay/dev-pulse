package dev.shounakmulay.devpulse.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.annotation.Factory

@Factory(binds = [DispatcherProvider::class])
internal class DPDispatcherProvider : DispatcherProvider {
    override val defaultDispatcher: CoroutineDispatcher
        get() = Dispatchers.Default
    override val ioDispatcher: CoroutineDispatcher
        get() = Dispatchers.IO
}