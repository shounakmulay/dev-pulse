package dev.shounakmulay.devpulse.buildsrc.plugins

import com.android.build.api.dsl.ApplicationExtension
import dev.shounakmulay.devpulse.buildsrc.extensions.libs
import dev.shounakmulay.devpulse.buildsrc.plugins.PluginExtensions.applyPlugin
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
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
                            jvmTarget.set(JvmTarget.JVM_11)
                        }
                    }
                }
            }

            plugins.withId("com.android.application") {
                extensions.configure<ApplicationExtension> {
                    compileSdk = libs.findVersion("android-compileSdk").get().requiredVersion.toInt()

                    defaultConfig {
                        minSdk = libs.findVersion("android-minSdk").get().requiredVersion.toInt()
                        targetSdk = libs.findVersion("android-targetSdk").get().requiredVersion.toInt()
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
                        sourceCompatibility = JavaVersion.VERSION_11
                        targetCompatibility = JavaVersion.VERSION_11
                    }
                }
            }
        }
    }
}
