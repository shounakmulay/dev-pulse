package dev.shounakmulay.devpulse.buildsrc.constants

object Modules {
    const val COMPOSE_APP = ":composeApp"
    const val ANDROID_APP = ":androidApp"

    object Feature {
        const val DEVTOOLS = ":feature:devtools"
        const val HOME = ":feature:home"
        const val FEED = ":feature:feed"
        const val SETTINGS = ":feature:settings"
    }

    object Core {
        const val DESIGN_SYSTEM = ":core:designsystem"
        const val NAVIGATION = ":core:navigation"
        const val UI = ":core:ui"
        const val RESOURCES = ":core:resources"
        const val NETWORK = ":core:network"
        const val PREFERENCES = ":core:preferences"
        const val COMMON = ":core:common"

        object Domain {
            const val MODELS = ":core:domain:models"
            const val SETTINGS = ":core:domain:settings"
        }

        object Data {
            const val SETTINGS = ":core:data:settings"
            const val FEED = ":core:data:feed"
            const val DB = ":core:data:db"
        }
    }
}
