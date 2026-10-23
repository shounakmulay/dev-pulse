import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import dev.shounakmulay.devpulse.buildsrc.constants.buildConfig
import dev.shounakmulay.devpulse.buildsrc.extensions.iosFrameworks

plugins {
    alias(libs.plugins.devpulse.kmp.library)
    alias(libs.plugins.buildKonfig)
}

buildkonfig {
    packageName = "dev.shounakmulay.devpulse.core.common"
    exposeObjectWithName = "BuildKonfigCommon"
    objectName = "BuildKonfig"
    defaultConfigs {
        buildConfigField(
            type = STRING,
            name = "ENVIRONMENT",
            value = project.buildConfig.environment,
            const = true
        )
        buildConfigField(
            type = STRING,
            name = "APP_VERSION",
            value = project.buildConfig.versionName,
            const = true
        )
        buildConfigField(
            type = STRING,
            name = "GITHUB_LINK",
            value = "https://github.com/shounakmulay/dev-pulse",
            const = true
        )
    }
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
