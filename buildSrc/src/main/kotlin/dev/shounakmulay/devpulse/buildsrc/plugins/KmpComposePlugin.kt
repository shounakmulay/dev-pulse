package dev.shounakmulay.devpulse.buildsrc.plugins

import dev.shounakmulay.devpulse.buildsrc.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
class KmpComposePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.plugin.compose")
                apply("org.jetbrains.compose")
                apply("org.jetbrains.compose.hot-reload")
            }

            plugins.withId("org.jetbrains.kotlin.multiplatform") {
                extensions.configure<KotlinMultiplatformExtension> {
                    sourceSets.getByName("commonMain") {
                        dependencies {
                            implementation(libs.findLibrary("compose-runtime").get())
                            implementation(libs.findLibrary("compose-foundation").get())
                            implementation(libs.findLibrary("compose-material3").get())
                            implementation(libs.findLibrary("compose-ui").get())
                            implementation(libs.findLibrary("compose-components-resources").get())
                            implementation(libs.findLibrary("compose-uiToolingPreview").get())
                            implementation(libs.findLibrary("androidx-lifecycle-viewmodelCompose").get())
                            implementation(libs.findLibrary("androidx-lifecycle-runtimeCompose").get())
                        }
                    }
                }
            }

            val project = this
            configurations.configureEach {
                if (name == "debugImplementation") {
                    project.dependencies.add(name, libs.findLibrary("compose-uiTooling").get())
                }
            }
        }
    }
}
