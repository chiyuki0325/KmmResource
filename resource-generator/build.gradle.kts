plugins {
    `java-gradle-plugin`
    alias(libs.plugins.jetbrainsKotlinJvm)
}

dependencies {
    implementation(gradleKotlinDsl())
    compileOnly(libs.kotlinGradlePlugin)
    compileOnly(libs.androidGradlePlugin)
    compileOnly(libs.androidSdkCommon)
    implementation(libs.kotlinPoet)
    implementation(libs.kotlinxSerialization)
    implementation(libs.apacheCommonsText)
    implementation(libs.commonsCodec)
    implementation(libs.gson)
}

gradlePlugin {
    plugins {
        create("multiplatform-resources") {
            id = "com.tencent.kuiklybase.resource.generator"
            implementationClass = "com.tencent.tmm.kmmgradle.MultiplatformResourcesPlugin"
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
