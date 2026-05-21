import dev.shounakmulay.devpulse.buildsrc.extensions.iosFrameworks

plugins {
    alias(libs.plugins.devpulse.kmp.library)
}

kotlin {
    android {
        namespace = "dev.shounakmulay.devpulse.core.common"
    }
    iosFrameworks(baseName = "core:common")

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.datetime)
        }
    }
}
