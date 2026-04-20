package dev.shounakmulay.devpulse.buildsrc.plugins

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
                applyPlugin(libs.findPlugin("kotlinMultiplatform"))
                applyPlugin(libs.findPlugin("kotlinSerialization"))
                applyPlugin(libs.findPlugin("koin-compiler"))
            }
            with(pluginManager) {
                apply("devpulse.kmp.android.library")
                apply("devpulse.kmp.ios")
                apply("devpulse.kmp.jvm")
            }

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
