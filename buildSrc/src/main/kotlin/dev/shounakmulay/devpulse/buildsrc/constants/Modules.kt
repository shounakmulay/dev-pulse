package dev.shounakmulay.devpulse.buildsrc.constants

object Modules {
    const val COMPOSE_APP = ":composeApp"
    const val ANDROID_APP = ":androidApp"

    object Bridge {
        const val MARKDOWN_CONVERTER = ":bridge:markdownconverter"
        const val READABILITY = ":bridge:readability"
        const val SQLITE_VEC = ":bridge:sqlitevec"
    }

    object Feature {
        const val DEVTOOLS = ":feature:devtools"
        const val HOME = ":feature:home"
        const val FEED = ":feature:feed"
        const val SETTINGS = ":feature:settings"
    }

    object Core {
        const val DESIGN_SYSTEM = ":core:designsystem"
        const val WEBVIEW = ":core:webview"
        const val NAVIGATION = ":core:navigation"
        const val UI = ":core:ui"
        const val RESOURCES = ":core:resources"
        const val LOGGING = ":core:logging"
        const val NETWORK = ":core:network"
        const val NOTIFICATIONS = ":core:notifications"
        const val PERMISSIONS = ":core:permissions"
        const val COMMON = ":core:common"
        const val SYNC = ":core:sync"
        const val REMOTE_CONFIG = ":core:remoteconfig"

        object Telemetry {
            const val API = ":core:telemetry:api"
            const val RUNTIME = ":core:telemetry:runtime"
        }

        object Domain {
            const val FEED = ":core:domain:feed"
            const val MODELS = ":core:domain:models"
            const val SETTINGS = ":core:domain:settings"
        }

        object Data {
            const val PREFERENCES = ":core:data:preferences"
            const val SETTINGS = ":core:data:settings"
            const val FEED = ":core:data:feed"
            const val DB = ":core:data:db"
        }
    }
}
