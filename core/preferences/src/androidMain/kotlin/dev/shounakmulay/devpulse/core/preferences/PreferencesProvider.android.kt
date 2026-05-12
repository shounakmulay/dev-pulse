package dev.shounakmulay.devpulse.core.preferences

import android.content.Context
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext

actual fun getFilePath(fileName: String): String {
    val context = GlobalContext.get().get<Context>()
    return context.filesDir.resolve(fileName).absolutePath
}