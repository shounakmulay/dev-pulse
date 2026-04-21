package dev.shounakmulay.devpulse.buildsrc.plugins

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
class KmpLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply {
                applyPlugin("kotlinMultiplatform", libs.findPlugin("kotlinMultiplatform"))
                applyPlugin("kotlinSerialization", libs.findPlugin("kotlinSerialization"))
                applyPlugin("koin-compiler", libs.findPlugin("koin-compiler"))
            }
            configureAndroidKmpLibrary()
            configureIosTargets()
            configureJvmTarget()

            plugins.withId("org.jetbrains.kotlin.multiplatform") {
                extensions.configure<KotlinMultiplatformExtension> {
                    sourceSets.getByName("commonMain") {
                        dependencies {
                            implementation(libs.findLibrary("kotlin-stdlib").get())
                            implementation(project.dependencies.platform(libs.findLibrary("koin-bom").get()))
                            implementation(libs.findLibrary("koin-core").get())
                            implementation(libs.findLibrary("koin-annotations").get())
                        }
                    }
                    sourceSets.getByName("commonTest") {
                        dependencies {
                            implementation(libs.findLibrary("kotlin-test").get())
                        }
                    }
                }
            }
        }
    }
}
