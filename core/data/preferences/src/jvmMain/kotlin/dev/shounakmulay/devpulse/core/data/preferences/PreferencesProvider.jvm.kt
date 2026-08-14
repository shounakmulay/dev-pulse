package dev.shounakmulay.devpulse.core.data.preferences

import java.io.File

actual fun getFilePath(fileName: String): String {
    return File(System.getProperty("java.io.tmpdir"), dataStoreFileName).absolutePath
}