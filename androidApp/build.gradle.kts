import com.android.build.api.dsl.ApplicationExtension
import dev.shounakmulay.devpulse.buildsrc.constants.Modules
import dev.shounakmulay.devpulse.buildsrc.constants.buildConfig
import java.util.Properties

plugins {
    alias(libs.plugins.devpulse.kmp.android.application)
    alias(libs.plugins.devpulse.kmp.compose)
}

configure<ApplicationExtension> {
    namespace = "dev.shounakmulay.devpulse.android"
    defaultConfig {
        applicationId = "dev.shounakmulay.devpulse"
        versionCode = buildConfig.android.versionCode
        versionName = buildConfig.android.versionName
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

    @TaskAction
    fun validate() {
        require(configured.get()) {
            "Set DEVPULSE_SIGNING_PROPERTIES to your private upload signing properties file."
        }
    }
}

val uploadSigningPropertiesPath = providers.environmentVariable("DEVPULSE_SIGNING_PROPERTIES")
    .orElse(providers.gradleProperty("devpulse.signingProperties"))
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
}

tasks.matching {
    it.name in setOf("bundleRelease", "assembleRelease", "packageReleaseBundle", "packageRelease", "signReleaseBundle")
}.configureEach {
    dependsOn(validateUploadSigning)
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
