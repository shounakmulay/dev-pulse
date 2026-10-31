import dev.shounakmulay.devpulse.buildsrc.constants.buildConfig

plugins {
    alias(libs.plugins.spotless)
    alias(libs.plugins.detekt)
}

spotless {
    ratchetFrom("da167f6ccefe45154ecf13ce567d00019b1b63bb")
    lineEndings = com.diffplug.spotless.LineEnding.UNIX
    kotlin {
        target("**/src/**/*.kt")
        targetExclude("**/build/**", "**/generated/**", "**/.gradle/**", "**/.codegraph/**", "bridge/readability/**", "**/src/*Test*/**", "**/src/test*/**")
        ktlint(libs.versions.ktlint.get())
    }
    kotlinGradle {
        target("**/*.gradle.kts")
        targetExclude("**/build/**", "**/.gradle/**", "**/.codegraph/**", "bridge/readability/**", "**/src/*Test*/**", "**/src/test*/**")
        ktlint(libs.versions.ktlint.get())
    }
}

dependencies {
    detektPlugins(libs.compose.rules.detekt)
    detektPlugins(project(":tools:detektrules"))
}

detekt {
    toolVersion = libs.versions.detekt.get()
    source.setFrom(
        fileTree(layout.projectDirectory) {
            include("**/src/**/*.kt", "**/*.gradle.kts")
            exclude("**/build/**", "**/generated/**", "**/.gradle/**", "**/.codegraph/**", "bridge/readability/**", "**/src/*Test*/**", "**/src/test*/**")
        },
    )
    config.setFrom(files("config/detekt.yml"))
    baseline = file("config/detekt-baseline.xml")
    buildUponDefaultConfig = false
}

tasks.register("qualityQuick") {
    group = "verification"
    description = "Checks formatting and Kotlin/Compose issues without compiling application code."
    dependsOn("spotlessCheck", "detekt", ":tools:detektrules:test")
}

tasks.register("qualityCheck") {
    group = "verification"
    description = "Runs quick checks, production Android lint and JVM compilation."
    dependsOn("qualityQuick", ":androidApp:lintStagingDebug")
}

val generateIosXcconfig by tasks.registering {
    group = "ios"
    description = "Generates iosApp/Configuration/Config.xcconfig from the version catalog."

    val outputFile = layout.projectDirectory.file("iosApp/Configuration/Config.xcconfig").asFile
    val currentProjectVersion = buildConfig.ios.currentProjectVersion
    val marketingVersion = buildConfig.ios.marketingVersion
    val deploymentTarget = buildConfig.ios.deploymentTarget

    inputs.property("currentProjectVersion", currentProjectVersion)
    inputs.property("marketingVersion", marketingVersion)
    inputs.property("deploymentTarget", deploymentTarget)
    outputs.file(outputFile)

    doLast {
        outputFile.writeText(
            """
            TEAM_ID=

            PRODUCT_NAME=DevPulse
            PRODUCT_BUNDLE_IDENTIFIER=dev.shounakmulay.devpulse.DevPulse

            CURRENT_PROJECT_VERSION=$currentProjectVersion
            MARKETING_VERSION=$marketingVersion
            IPHONEOS_DEPLOYMENT_TARGET=$deploymentTarget
            """.trimIndent() + "\n",
        )
    }
}

subprojects {
    val moduleChecks = tasks.matching {
        it.name == "lintAndroidMain" || it.name == "compileKotlinJvm"
    }
    if (path != ":bridge:readability") {
        rootProject.tasks.named("qualityCheck") {
            dependsOn(moduleChecks)
        }
    }
    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        tasks.matching { task ->
            task.name.startsWith("link") ||
                task.name.startsWith("embedAndSign") ||
                task.name.startsWith("syncFramework") ||
                task.name == "preBuild" ||
                task.name == "assemble"
        }.configureEach {
            dependsOn(generateIosXcconfig)
        }
    }
}
