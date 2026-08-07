import com.android.build.api.dsl.ApplicationExtension
import dev.shounakmulay.devpulse.buildsrc.constants.Modules
import dev.shounakmulay.devpulse.buildsrc.constants.buildConfig

plugins {
    alias(libs.plugins.devpulse.kmp.android.application)
    alias(libs.plugins.devpulse.kmp.compose)
}

configure<ApplicationExtension> {
    namespace = "dev.shounakmulay.devpulse.android"
    defaultConfig {
        applicationId = "dev.shounakmulay.devpulse"
        versionCode = buildConfig.android.versionCode
        versionName = buildConfig.android.versionName
    }
}

dependencies {
    implementation(project(Modules.COMPOSE_APP))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
}
android {
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }

    defaultConfig {
        ndk {
            // Strips 32-bit binaries from transitive dependencies
            // and forces the app to run strictly in 64-bit mode.
            abiFilters.clear()
            abiFilters += setOf("arm64-v8a", "x86_64")
        }
    }
}
