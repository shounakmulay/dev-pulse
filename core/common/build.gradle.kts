import dev.shounakmulay.devpulse.buildsrc.constants.buildConfig
import dev.shounakmulay.devpulse.buildsrc.extensions.iosFrameworks

plugins {
    alias(libs.plugins.devpulse.kmp.library)
    alias(libs.plugins.buildConfig)
}

buildConfig {
    packageName("dev.shounakmulay.devpulse.core.common")
    useKotlinOutput { internalVisibility = false }
    buildConfigField("APP_VERSION", project.buildConfig.versionName)
    buildConfigField("GITHUB_LINK", "https://github.com/shounakmulay/dev-pulse")
}

kotlin {
    android {
        namespace = "dev.shounakmulay.devpulse.core.common"
    }
    iosFrameworks(baseName = "core:common")

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.collections.immutable)
        }
    }
}
