import org.gradle.api.publish.PublishingExtension

plugins {
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.jetbrainsKotlinJvm) apply false
    alias(libs.plugins.composeOhos) apply false
    alias(libs.plugins.composeCompiler) apply false
}

allprojects {
    group = "com.tencent.kuiklybase"
    version = "0.0.1-neupass.17"

    plugins.withId("maven-publish") {
        extensions.configure<PublishingExtension> {
            repositories {
                maven {
                    name = "local"
                    url = uri(
                        providers.gradleProperty("localMavenRepository")
                            .getOrElse(
                                rootProject.layout.buildDirectory.dir("maven-repository")
                                    .get().asFile.absolutePath,
                            ),
                    )
                }
            }
        }
    }
}
