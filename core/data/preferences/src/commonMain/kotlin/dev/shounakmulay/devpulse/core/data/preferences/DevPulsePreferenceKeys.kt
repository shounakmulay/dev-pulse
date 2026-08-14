package dev.shounakmulay.devpulse.core.data.preferences

object DevPulsePreferenceKeys {
    val isAppInBlackMode = DevPulsePreferenceKey.BooleanPreferenceKey("isAppInBlackMode")
    val appTheme = DevPulsePreferenceKey.StringPreferenceKey("appTheme")
    val reImportExistingPosts =
        DevPulsePreferenceKey.BooleanPreferenceKey("reImportExistingPosts")
    val contentTextScale = DevPulsePreferenceKey.FloatPreferenceKey("contentTextScale")
    val lineHeightScale = DevPulsePreferenceKey.FloatPreferenceKey("lineHeightScale")
}