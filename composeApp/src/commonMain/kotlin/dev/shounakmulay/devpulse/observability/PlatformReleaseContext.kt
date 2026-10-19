package dev.shounakmulay.devpulse.observability

import dev.shounakmulay.devpulse.AppBuildConfiguration
import dev.shounakmulay.devpulse.core.common.BuildKonfigCommon
import dev.shounakmulay.devpulse.core.telemetry.api.AppReleaseContext

internal expect fun platformReleaseContext(): AppReleaseContext

internal fun appReleaseContext(
    platform: String,
    buildNumber: String,
    appVersion: String = BuildKonfigCommon.APP_VERSION,
    appId: String = "dev.shounakmulay.devpulse",
): AppReleaseContext = AppReleaseContext(
    appId = appId,
    platform = platform,
    appVersion = appVersion,
    buildNumber = buildNumber,
    environment = AppBuildConfiguration.ENVIRONMENT,
    channel = AppBuildConfiguration.RELEASE_CHANNEL,
    commitId = AppBuildConfiguration.COMMIT_ID.takeUnless { it == "local" },
)
