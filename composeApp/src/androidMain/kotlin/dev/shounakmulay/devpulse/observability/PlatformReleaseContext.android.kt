package dev.shounakmulay.devpulse.observability

import dev.shounakmulay.devpulse.AppBuildConfiguration
import dev.shounakmulay.devpulse.core.telemetry.api.AppReleaseContext

internal actual fun platformReleaseContext(): AppReleaseContext = appReleaseContext(
    platform = "android",
    buildNumber = AppBuildConfiguration.ANDROID_BUILD,
)
