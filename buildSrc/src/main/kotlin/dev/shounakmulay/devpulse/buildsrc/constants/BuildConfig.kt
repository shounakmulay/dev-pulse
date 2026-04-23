package dev.shounakmulay.devpulse.buildsrc.constants

import dev.shounakmulay.devpulse.buildsrc.extensions.libs
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

class BuildConfig(private val project: Project) {
    val android = Android()
    val jvm = Jvm()
    val desktop = Desktop()
    val ios = Ios()

    inner class Android {
        val compileSdk: Int get() = version("android-compileSdk").toInt()
        val minSdk: Int get() = version("android-minSdk").toInt()
        val targetSdk: Int get() = version("android-targetSdk").toInt()
        val versionCode: Int get() = version("app-versionCode").toInt()
        val versionName: String get() = version("app-versionName")
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
