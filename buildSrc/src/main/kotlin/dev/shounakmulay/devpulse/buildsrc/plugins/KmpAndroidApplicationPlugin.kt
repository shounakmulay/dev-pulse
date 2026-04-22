package dev.shounakmulay.devpulse.buildsrc.plugins

import com.android.build.api.dsl.ApplicationExtension
import dev.shounakmulay.devpulse.buildsrc.constants.buildConfig
import dev.shounakmulay.devpulse.buildsrc.extensions.libs
import dev.shounakmulay.devpulse.buildsrc.plugins.PluginExtensions.applyPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
class KmpAndroidApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.applyPlugin("androidApplication", libs.findPlugin("androidApplication"))

            plugins.withId("org.jetbrains.kotlin.multiplatform") {
                extensions.configure<KotlinMultiplatformExtension> {
                    androidTarget {
                        compilerOptions {
                            jvmTarget.set(buildConfig.jvm.target)
                        }
                    }
                }
            }

            plugins.withId("com.android.application") {
                extensions.configure<ApplicationExtension> {
                    compileSdk = buildConfig.android.compileSdk

                    defaultConfig {
                        minSdk = buildConfig.android.minSdk
                        targetSdk = buildConfig.android.targetSdk
                    }

                    packaging {
                        resources {
                            excludes += "/META-INF/{AL2.0,LGPL2.1}"
                        }
                    }

                    buildTypes {
                        getByName("release") {
                            isMinifyEnabled = false
                        }
                    }

                    compileOptions {
                        sourceCompatibility = buildConfig.jvm.javaVersion
                        targetCompatibility = buildConfig.jvm.javaVersion
                    }
                }
            }
        }
    }
}
