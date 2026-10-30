package dev.shounakmulay.devpulse.core.logging

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import dev.shounakmulay.devpulse.core.common.BuildKonfigCommon
import dev.shounakmulay.devpulse.core.logging.events.EventEntry
import dev.shounakmulay.devpulse.core.logging.events.EventSink
import org.koin.core.annotation.Factory

@Factory
internal class FirebaseEventSink(
    private val analytics: FirebaseAnalytics,
) : EventSink {
    override fun isTrackable(entry: EventEntry): Boolean = true

    override fun track(entry: EventEntry) {
        val parameters = Bundle().apply {
            entry.data.forEach { (key, value) -> putString(key, value) }
            putString("environment", BuildKonfigCommon.ENVIRONMENT)
        }
        analytics.logEvent(entry.name, parameters)
    }
}
