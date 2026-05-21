package dev.shounakmulay.devpulse.buildsrc.plugins

import androidx.room.gradle.RoomExtension
import dev.shounakmulay.devpulse.buildsrc.extensions.configureAndroidKmpLibrary
import dev.shounakmulay.devpulse.buildsrc.extensions.configureIosTargets
import dev.shounakmulay.devpulse.buildsrc.extensions.configureJvmTarget
import dev.shounakmulay.devpulse.buildsrc.extensions.libs
import dev.shounakmulay.devpulse.buildsrc.plugins.PluginExtensions.applyPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
class KmpRoomPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply {
                applyPlugin("devpulse-kmp-library", libs.findPlugin("devpulse-kmp-library"))
                applyPlugin("ksp", libs.findPlugin("ksp"))
                applyPlugin("androidx-room", libs.findPlugin("androidx-room"))
            }

            configureAndroidKmpLibrary()
            configureIosTargets()
            configureJvmTarget()

            extensions.configure<RoomExtension> {
                schemaDirectory("$projectDir/schemas")
            }

            dependencies.add("kspAndroid", libs.findLibrary("androidx-room-compiler").get())
            dependencies.add("kspIosArm64", libs.findLibrary("androidx-room-compiler").get())
            dependencies.add("kspIosSimulatorArm64", libs.findLibrary("androidx-room-compiler").get())
            dependencies.add("kspJvm", libs.findLibrary("androidx-room-compiler").get())

            plugins.withId("org.jetbrains.kotlin.multiplatform") {
                extensions.configure<KotlinMultiplatformExtension> {
                    sourceSets.getByName("commonMain") {
                        dependencies {
                            implementation(libs.findLibrary("androidx-room-runtime").get())
                            implementation(libs.findLibrary("androidx-room-sqlite-bundled").get())
                        }
                    }
                }
            }
        }
    }
}
