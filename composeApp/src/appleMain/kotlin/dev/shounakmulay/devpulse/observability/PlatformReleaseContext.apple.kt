package dev.shounakmulay.devpulse.observability

import dev.shounakmulay.devpulse.AppBuildConfiguration
import dev.shounakmulay.devpulse.core.common.BuildKonfigCommon
import dev.shounakmulay.devpulse.core.telemetry.api.AppReleaseContext
import platform.Foundation.NSBundle

internal actual fun platformReleaseContext(): AppReleaseContext = appReleaseContext(
    platform = "ios",
    buildNumber = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleVersion") as? String
        ?: AppBuildConfiguration.IOS_BUILD,
    appVersion = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String
        ?: BuildKonfigCommon.APP_VERSION,
    appId = NSBundle.mainBundle.bundleIdentifier ?: "dev.shounakmulay.devpulse",
)
