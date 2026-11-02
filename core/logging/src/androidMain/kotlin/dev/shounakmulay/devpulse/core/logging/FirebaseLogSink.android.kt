package dev.shounakmulay.devpulse.core.logging

import android.content.Context
import android.os.Build
import com.google.firebase.crashlytics.CustomKeysAndValues
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dev.shounakmulay.devpulse.core.common.BuildKonfigCommon
import dev.shounakmulay.devpulse.core.logging.consent.AnalyticsConsentProvider
import dev.shounakmulay.devpulse.core.logging.log.DPLogKey
import dev.shounakmulay.devpulse.core.logging.log.DPLogLevel
import dev.shounakmulay.devpulse.core.logging.log.LogEntry
import dev.shounakmulay.devpulse.core.logging.log.LogSink
import org.koin.core.annotation.Factory

@Factory
internal class FirebaseLogSink(
    private val crashlytics: FirebaseCrashlytics,
    context: Context,
    private val consentProvider: AnalyticsConsentProvider,
) : LogSink {
    private val defaultData: Map<DPLogKey, String>

    init {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val build = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            packageInfo.versionCode.toLong()
        }
        defaultData = mapOf(
            DPLogKey.PLATFORM to "android",
            DPLogKey.ENVIRONMENT to BuildKonfigCommon.ENVIRONMENT,
            DPLogKey.APP_VERSION to packageInfo.versionName.orEmpty(),
            DPLogKey.APP_BUILD to build.toString(),
        )
    }

    override fun isLoggable(entry: LogEntry): Boolean = consentProvider.getConsent()?.crashReporting == true && entry.level >= DPLogLevel.Error

    override fun log(entry: LogEntry) {
        if (!isLoggable(entry)) return
        val data = defaultData + mapOf(
            DPLogKey.LOGGER_NAME to entry.tag,
            DPLogKey.LOG_MESSAGE to entry.message,
            DPLogKey.LOG_LEVEL to entry.level.name,
        ) + entry.data
        val throwable = entry.throwable
        if (throwable == null) {
            val context = data.entries.sortedBy { it.key.wireName }.joinToString { (key, value) -> "${key.wireName}=$value" }
            crashlytics.log("${entry.tag}: ${entry.message} {$context}")
        } else {
            val keys = CustomKeysAndValues.Builder().apply {
                data.forEach { (key, value) -> putString(key.wireName, value) }
            }.build()
            crashlytics.recordException(throwable, keys)
        }
    }
}
