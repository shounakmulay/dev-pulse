package dev.shounakmulay.devpulse.buildsrc.constants

import dev.shounakmulay.devpulse.buildsrc.extensions.libs
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

class BuildConfig(private val project: Project) {
    val environment: String
        get() {
            require(!project.providers.gradleProperty("buildKonfig.flavor").isPresent) {
                "Use buildkonfig.flavor (lowercase), not buildKonfig.flavor."
            }
            return project.providers.gradleProperty("buildkonfig.flavor").orElse("staging").get().also {
                require(it in setOf("staging", "production")) {
                    "Unknown environment '$it'. Set -Pbuildkonfig.flavor=staging or production."
                }
            }
        }
    val versionName: String get() = version("app-versionName")
    val android = Android()
    val jvm = Jvm()
    val desktop = Desktop()
    val ios = Ios()

    inner class Android {
        val compileSdk: Int get() = version("android-compileSdk").toInt()
        val minSdk: Int get() = version("android-minSdk").toInt()
        val targetSdk: Int get() = version("android-targetSdk").toInt()
        val versionCode: Int
            get() {
                val value = project.providers.environmentVariable("DEVPULSE_VERSION_CODE")
                    .orElse(version("app-versionCode")).get()
                require(value.matches(Regex("[0-9]+"))) {
                    "DEVPULSE_VERSION_CODE must contain only decimal digits."
                }
                return requireNotNull(value.toIntOrNull()?.takeIf { it in 1..2_100_000_000 }) {
                    "Android versionCode must be between 1 and 2100000000."
                }
            }
        val versionName: String get() = this@BuildConfig.versionName
    }

    inner class Jvm {
        val target: JvmTarget get() = JvmTarget.fromTarget(version("jvm-target"))
        val javaVersion: JavaVersion get() = JavaVersion.toVersion(version("jvm-target"))
    }

    inner class Desktop {
        val packageVersion: String get() = version("app-desktopPackageVersion")
    }

    inner class Ios {
        val deploymentTarget: String get() = version("ios-deploymentTarget")
        val marketingVersion: String get() = version("ios-marketingVersion")
        val currentProjectVersion: String get() = version("ios-currentProjectVersion")
    }

    private fun version(alias: String): String =
        project.libs.findVersion(alias).get().requiredVersion
}

val Project.buildConfig: BuildConfig get() = BuildConfig(this)
