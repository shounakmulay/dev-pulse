package dev.shounakmulay.devpulse.core.logging

import android.content.Context
import android.os.Build
import com.google.firebase.crashlytics.CustomKeysAndValues
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dev.shounakmulay.devpulse.core.common.BuildKonfigCommon
import dev.shounakmulay.devpulse.core.logging.log.DPLogLevel
import dev.shounakmulay.devpulse.core.logging.log.LogEntry
import dev.shounakmulay.devpulse.core.logging.log.LogSink
import org.koin.core.annotation.Factory

@Factory
internal class FirebaseLogSink(
    private val crashlytics: FirebaseCrashlytics,
    context: Context,
) : LogSink {
    private val defaultData: Map<String, String>

    init {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val build = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            packageInfo.versionCode.toLong()
        }
        defaultData = mapOf(
            "platform" to "android",
            "environment" to BuildKonfigCommon.ENVIRONMENT,
            "app.version" to packageInfo.versionName.orEmpty(),
            "app.build" to build.toString(),
        )
    }

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
