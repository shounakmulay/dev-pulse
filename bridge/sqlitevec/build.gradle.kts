import dev.shounakmulay.devpulse.buildsrc.constants.Modules
import dev.shounakmulay.devpulse.buildsrc.extensions.iosFrameworks

plugins {
    alias(libs.plugins.devpulse.kmp.library)
}

kotlin {
    android {
        namespace = "dev.shounakmulay.devpulse.bridge.sqlitevec"
    }
    iosFrameworks(baseName = "bridge:sqlitevec")

    sourceSets {
        commonMain.dependencies {
            implementation(project(Modules.Core.COMMON))
        }
        androidMain.dependencies {
            implementation(libs.sqlite.vector)
        }
    }
}
