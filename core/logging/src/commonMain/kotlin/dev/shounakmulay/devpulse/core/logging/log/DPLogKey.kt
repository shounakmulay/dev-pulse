package dev.shounakmulay.devpulse.core.logging.log

enum class DPLogKey(val wireName: String) {
    PLATFORM("platform"),
    ENVIRONMENT("environment"),
    APP_VERSION("app.version"),
    APP_BUILD("app.build"),
    LOGGER_NAME("logger.name"),
    LOG_MESSAGE("log.message"),
    LOG_LEVEL("log.level"),
    KOTLIN_STACKTRACE("kotlin_stacktrace"),
    FEED_ID("feed_id"),
}
