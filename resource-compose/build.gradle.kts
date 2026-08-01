plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeOhos)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget()
    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(libs.override.compose.runtime)
            api(libs.override.compose.ui)
            api(libs.override.compose.foundation)
            api(project(":resource-core"))
        }
        androidMain.dependencies { implementation(libs.appCompatResources) }
        val appleMain by creating { dependsOn(commonMain.get()) }
        val iosMain by creating { dependsOn(appleMain) }
        iosX64Main.get().dependsOn(iosMain)
        iosArm64Main.get().dependsOn(iosMain)
        iosSimulatorArm64Main.get().dependsOn(iosMain)
        // ohosArm64Main is retained but disabled with the standard toolchain.
    }
}

android {
    namespace = "com.tencent.tmm.kmmresource.compose"
    compileSdk = 35
    defaultConfig { minSdk = 21 }
}
