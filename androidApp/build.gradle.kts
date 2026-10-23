import com.android.build.api.dsl.ApplicationExtension
import com.google.gson.Gson
import com.google.gson.JsonObject
import dev.shounakmulay.devpulse.buildsrc.constants.Modules
import dev.shounakmulay.devpulse.buildsrc.constants.buildConfig
import java.util.Properties

plugins {
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.devpulse.kmp.android.application)
    alias(libs.plugins.devpulse.kmp.compose)
}

val environment = buildConfig.environment

configure<ApplicationExtension> {
    namespace = "dev.shounakmulay.devpulse.android"
    defaultConfig {
        applicationId = "dev.shounakmulay.devpulse"
        versionCode = buildConfig.android.versionCode
        versionName = buildConfig.android.versionName
    }
    flavorDimensions += "environment"
    productFlavors {
        create("staging") {
            dimension = "environment"
            applicationIdSuffix = ".staging"
        }
        create("production") {
            dimension = "environment"
        }
    }
}

androidComponents {
    beforeVariants(selector().all()) { variant ->
        variant.enable = variant.productFlavors.any { (dimension, flavor) ->
            dimension == "environment" && flavor == environment
        }
    }
}

dependencies {
    implementation(project(Modules.COMPOSE_APP))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.startup.runtime)
}

abstract class ValidateUploadSigning : DefaultTask() {
    @get:Input
    abstract val configured: Property<Boolean>

    @get:Input
    abstract val settingsName: Property<String>

    @TaskAction
    fun validate() {
        require(configured.get()) {
            "Set ${settingsName.get()} to your private release signing properties file."
        }
    }
}

abstract class ValidateFirebaseConfiguration : DefaultTask() {
    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val configurationFile: RegularFileProperty

    @get:Input
    abstract val expectedProject: Property<String>

    @get:Input
    abstract val expectedPackage: Property<String>

    @TaskAction
    fun validate() {
        val configuration = Gson().fromJson(configurationFile.get().asFile.readText(), JsonObject::class.java)
        require(configuration.getAsJsonObject("project_info").get("project_id").asString == expectedProject.get()) {
            "Firebase project does not match the selected environment."
        }
        require(configuration.getAsJsonArray("client").any {
            it.asJsonObject.getAsJsonObject("client_info")
                .getAsJsonObject("android_client_info").get("package_name").asString == expectedPackage.get()
        }) {
            "Firebase Android package does not match the selected environment."
        }
    }
}

val signingSettingsName = when (environment) {
    "staging" -> "DEVPULSE_STAGING_SIGNING_PROPERTIES"
    else -> "DEVPULSE_SIGNING_PROPERTIES"
}
val signingPropertyName = when (environment) {
    "staging" -> "devpulse.stagingSigningProperties"
    else -> "devpulse.signingProperties"
}
val uploadSigningPropertiesPath = providers.environmentVariable(signingSettingsName)
    .orElse(providers.gradleProperty(signingPropertyName))
val uploadSigningProperties = Properties().apply {
    uploadSigningPropertiesPath.orNull?.let { path ->
        file(path).inputStream().use { load(it) }
        listOf("storeFile", "storePassword", "keyAlias", "keyPassword").forEach { name ->
            require(!getProperty(name).isNullOrBlank()) { "Missing upload signing setting '$name'." }
        }
    }
}
val validateUploadSigning by tasks.registering(ValidateUploadSigning::class) {
    configured.set(uploadSigningPropertiesPath.isPresent)
    settingsName.set(signingSettingsName)
}

val validateFirebaseConfiguration by tasks.registering(ValidateFirebaseConfiguration::class) {
    configurationFile.set(layout.projectDirectory.file("src/$environment/google-services.json"))
    expectedProject.set(if (environment == "staging") "dev-pulse-b6b18-staging" else "dev-pulse-b6b18")
    expectedPackage.set("dev.shounakmulay.devpulse" + if (environment == "staging") ".staging" else "")
}

androidComponents {
    onVariants(selector().all()) { variant ->
        val variantName = variant.name.replaceFirstChar { it.uppercase() }
        tasks.matching { it.name == "process${variantName}GoogleServices" }.configureEach {
            dependsOn(validateFirebaseConfiguration)
        }
        if (variant.buildType == "release") {
            tasks.matching {
                it.name in setOf(
                    "assemble$variantName", "bundle$variantName", "package$variantName",
                    "package${variantName}Bundle", "sign${variantName}Bundle", "validateSigning$variantName"
                )
            }.configureEach {
                dependsOn(validateUploadSigning)
            }
        }
    }
}

android {
    signingConfigs {
        if (uploadSigningPropertiesPath.isPresent) {
            create("upload") {
                storeFile = file(uploadSigningProperties.getProperty("storeFile"))
                storePassword = uploadSigningProperties.getProperty("storePassword")
                keyAlias = uploadSigningProperties.getProperty("keyAlias")
                keyPassword = uploadSigningProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.findByName("upload")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            ndk {
                debugSymbolLevel = "SYMBOL_TABLE"
            }
        }
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }

    defaultConfig {
        ndk {
            // Strips 32-bit binaries from transitive dependencies
            // and forces the app to run strictly in 64-bit mode.
            abiFilters.clear()
            abiFilters += setOf("arm64-v8a", "x86_64")
        }
    }
}
