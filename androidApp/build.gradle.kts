import com.android.build.api.dsl.ApplicationExtension
import dev.shounakmulay.devpulse.buildsrc.constants.Modules
import dev.shounakmulay.devpulse.buildsrc.constants.buildConfig
import org.gradle.kotlin.dsl.configure

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
}
