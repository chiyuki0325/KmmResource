plugins {
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.jetbrainsKotlinJvm) apply false
    alias(libs.plugins.composeOhos) apply false
    alias(libs.plugins.composeCompiler) apply false
}

allprojects {
    group = "com.tencent.kuiklybase"
    version = "0.0.1-neupass.1"
}
