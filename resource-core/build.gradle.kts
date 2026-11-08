plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    `maven-publish`
}

val hasKotlinNativeHostDistribution =
    !System.getProperty("os.name").lowercase().contains("linux") ||
        System.getProperty("os.arch").lowercase() !in setOf("aarch64", "arm64")

kotlin {
    androidTarget {
        publishLibraryVariants("release")
    }
    jvm()
    if (hasKotlinNativeHostDistribution) {
        iosX64()
        iosArm64()
        iosSimulatorArm64()
    }

    sourceSets {
        val commonMain by getting
        val androidMain by getting {
            dependencies { implementation(libs.appCompatResources) }
        }
        val jvmMain by getting {
            dependencies {
                implementation(libs.kotlinxSerialization)
                implementation("com.ibm.icu:icu4j:77.1")
            }
        }
        val jvmTest by getting {
            dependencies { implementation(kotlin("test")) }
        }
        if (hasKotlinNativeHostDistribution) {
            val appleMain by creating { dependsOn(commonMain) }
            val iosMain by creating { dependsOn(appleMain) }
            iosX64Main.get().dependsOn(iosMain)
            iosArm64Main.get().dependsOn(iosMain)
            iosSimulatorArm64Main.get().dependsOn(iosMain)
        }
        // ohosArm64Main remains in source control. Re-enable with the
        // KuiklyBase 2.0.21-KBA-003 toolchain and KNOI plugin.
    }
}

android {
    namespace = "com.tencent.tmm.kmmresource"
    compileSdk = 35
    defaultConfig { minSdk = 21 }
}
