package dev.shounakmulay.devpulse.buildsrc.extensions

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import dev.shounakmulay.devpulse.buildsrc.plugins.PluginExtensions.applyPlugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureAndroidKmpLibrary() {
    plugins.apply {
        applyPlugin("androidKotlinMultiplatformLibrary", libs.findPlugin("androidKotlinMultiplatformLibrary"))
        applyPlugin("androidLint", libs.findPlugin("androidLint"))
    }

    plugins.withId("com.android.kotlin.multiplatform.library") {
        extensions.configure<KotlinMultiplatformExtension> {
            targets.withType(KotlinMultiplatformAndroidLibraryTarget::class.java)
                .configureEach {
                    compileSdk =
                        libs.findVersion("android-compileSdk").get().requiredVersion.toInt()
                    minSdk =
                        libs.findVersion("android-minSdk").get().requiredVersion.toInt()
                }
        }
    }

    // Device test deps are deferred — only added when a module opts in via withDeviceTestBuilder {}
    val project = this
    configurations.configureEach {
        if (name == "androidDeviceTestImplementation") {
            project.dependencies.add(name, libs.findLibrary("androidx-runner").get())
            project.dependencies.add(name, libs.findLibrary("androidx-core").get())
            project.dependencies.add(name, libs.findLibrary("androidx-testExt-junit").get())
        }
    }
}

internal fun Project.configureIosTargets() {
    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        extensions.configure<KotlinMultiplatformExtension> {
            iosArm64()
            iosSimulatorArm64()
        }
    }
}

internal fun Project.configureJvmTarget() {
    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        extensions.configure<KotlinMultiplatformExtension> {
            jvm()
        }
    }
}
