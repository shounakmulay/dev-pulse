package dev.shounakmulay.devpulse.buildsrc.plugins

import dev.shounakmulay.devpulse.buildsrc.extensions.libs
import dev.shounakmulay.devpulse.buildsrc.plugins.PluginExtensions.applyPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class KmpLibraryComposePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply {
            applyPlugin("devpulse-kmp-library", target.libs.findPlugin("devpulse-kmp-library"))
            applyPlugin("devpulse-kmp-compose", target.libs.findPlugin("devpulse-kmp-compose"))
        }
    }
}
