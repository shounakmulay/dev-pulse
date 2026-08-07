package dev.shounakmulay.devpulse.core.common.platform

enum class PlatformType {
    ANDROID, IOS, JVM
}

expect fun getPlatformType(): PlatformType