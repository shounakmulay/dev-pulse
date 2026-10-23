package dev.shounakmulay.devpulse.core.logging

import android.app.Application
import android.os.Build
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.CustomKeysAndValues
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dev.shounakmulay.devpulse.core.common.BuildKonfigCommon
import dev.shounakmulay.devpulse.core.logging.log.DPLogLevel
import dev.shounakmulay.devpulse.core.logging.log.LogEntry
import dev.shounakmulay.devpulse.core.logging.log.LogSink

internal fun createFirebaseLogSinks(application: Application): List<LogSink> {
    checkNotNull(FirebaseApp.initializeApp(application)) { "Firebase app configuration is missing" }
    val packageInfo = application.packageManager.getPackageInfo(application.packageName, 0)
    val build = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        packageInfo.longVersionCode
    } else {
        packageInfo.versionCode.toLong()
    }
    return listOf(
        FirebaseLogSink(
            crashlytics = FirebaseCrashlytics.getInstance(),
            defaultData = mapOf(
                "platform" to "android",
                "environment" to BuildKonfigCommon.ENVIRONMENT,
                "app.version" to packageInfo.versionName.orEmpty(),
                "app.build" to build.toString(),
            ),
        ),
    )
}

internal class FirebaseLogSink(
    private val crashlytics: FirebaseCrashlytics,
    defaultData: Map<String, String>,
) : LogSink {
    private val defaultData = defaultData.toMap()

    override fun isLoggable(entry: LogEntry): Boolean = entry.level >= DPLogLevel.Error

    override fun log(entry: LogEntry) {
        if (!isLoggable(entry)) return
        val data = defaultData + mapOf(
            "logger.name" to entry.tag,
            "log.message" to entry.message,
            "log.level" to entry.level.name,
        ) + entry.data
        val throwable = entry.throwable
        if (throwable == null) {
            val context = data.toSortedMap().entries.joinToString { (key, value) -> "$key=$value" }
            crashlytics.log("${entry.tag}: ${entry.message} {$context}")
        } else {
            val keys = CustomKeysAndValues.Builder().apply {
                data.forEach { (key, value) -> putString(key, value) }
            }.build()
            crashlytics.recordException(throwable, keys)
        }
    }
}
