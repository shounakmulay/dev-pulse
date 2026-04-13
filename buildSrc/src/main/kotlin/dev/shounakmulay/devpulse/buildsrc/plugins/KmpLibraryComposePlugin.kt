package dev.shounakmulay.devpulse.buildsrc.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class KmpLibraryComposePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target.pluginManager) {
            apply("devpulse.kmp.library")
            apply("devpulse.kmp.compose")
        }
    }
}
