import com.android.build.api.dsl.ApplicationExtension
import dev.shounakmulay.devpulse.buildsrc.constants.Modules
import org.gradle.kotlin.dsl.configure

plugins {
    alias(libs.plugins.devpulse.kmp.android.application)
    alias(libs.plugins.devpulse.kmp.compose)
}

configure<ApplicationExtension> {
    namespace = "dev.shounakmulay.devpulse.android"
    defaultConfig {
        applicationId = "dev.shounakmulay.devpulse"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(project(Modules.COMPOSE_APP))
    implementation(libs.androidx.activity.compose)
}
