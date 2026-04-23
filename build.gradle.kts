import dev.shounakmulay.devpulse.buildsrc.constants.buildConfig

// Convention plugins in buildSrc manage all plugin application for submodules.
// No top-level plugin declarations are needed here.

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
            PRODUCT_BUNDLE_IDENTIFIER=dev.shounakmulay.devpulse.DevPulse${'$'}(TEAM_ID)

            CURRENT_PROJECT_VERSION=$currentProjectVersion
            MARKETING_VERSION=$marketingVersion
            IPHONEOS_DEPLOYMENT_TARGET=$deploymentTarget
            """.trimIndent() + "\n"
        )
    }
}

subprojects {
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
