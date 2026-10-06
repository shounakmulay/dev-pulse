package dev.shounakmulay.devpulse.core.ui.service

import android.app.Service
import androidx.annotation.CallSuper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

abstract class CoroutineService : Service() {
    protected abstract val dispatcher: CoroutineDispatcher
    protected val serviceScope = CoroutineScope(SupervisorJob() + dispatcher)

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
