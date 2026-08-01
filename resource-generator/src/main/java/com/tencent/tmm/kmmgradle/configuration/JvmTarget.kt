package com.tencent.tmm.kmmgradle.configuration

import com.tencent.tmm.kmmgradle.generator.MRGenerator
import com.tencent.tmm.kmmgradle.generator.ResourceGeneratorFeature
import com.tencent.tmm.kmmgradle.generator.jvm.JvmMRGenerator
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import java.io.File

fun setupJvmGenerator(
    target: KotlinTarget,
    generatedDir: File,
    mrSettings: MRGenerator.MRSettings,
    features: List<ResourceGeneratorFeature<out MRGenerator.Generator>>,
    project: Project,
) {
    val sourceSet = target.compilations.getByName(KotlinCompilation.MAIN_COMPILATION_NAME).defaultSourceSet
    val generatorSourceSet = object : MRGenerator.SourceSet {
        override val name: String = sourceSet.name
        override fun addSourceDir(directory: File) { sourceSet.kotlin.srcDir(directory) }
        override fun addResourcesDir(directory: File) { sourceSet.resources.srcDir(directory) }
        override fun addAssetsDir(directory: File) { sourceSet.resources.srcDir(directory) }
    }
    JvmMRGenerator(
        generatedDir,
        generatorSourceSet,
        mrSettings,
        features.map { it.createJvmGenerator() },
    ).apply(project)
}
