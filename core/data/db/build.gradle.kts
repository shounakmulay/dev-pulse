import dev.shounakmulay.devpulse.buildsrc.constants.Modules
import dev.shounakmulay.devpulse.buildsrc.extensions.iosFrameworks

plugins {
    alias(libs.plugins.devpulse.kmp.library)
    id("com.google.devtools.ksp")
    alias(libs.plugins.androidx.room3)
}

kotlin {
    android {
        namespace = "dev.shounakmulay.devpulse.core.data.db"
    }
    iosFrameworks(baseName = "core:data:DbKit")

    sourceSets {
        commonMain.dependencies {
            implementation(project(Modules.Core.COMMON))

            implementation(libs.androidx.room3.runtime)
            implementation(libs.androidx.room.sqlite.bundled)
        }
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room3.compiler)
    add("kspIosArm64", libs.androidx.room3.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room3.compiler)
    add("kspJvm", libs.androidx.room3.compiler)
}

room3 {
    schemaDirectory("$projectDir/schemas")
}
