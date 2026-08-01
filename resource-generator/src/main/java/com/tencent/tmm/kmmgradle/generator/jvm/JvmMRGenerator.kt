package com.tencent.tmm.kmmgradle.generator.jvm

import com.squareup.kotlinpoet.KModifier
import com.tencent.tmm.kmmgradle.generator.MRGenerator
import org.gradle.api.Project
import org.gradle.api.Task

class JvmMRGenerator(
    generatedDir: java.io.File,
    sourceSet: SourceSet,
    mrSettings: MRSettings,
    generators: List<Generator>,
) : MRGenerator(generatedDir, sourceSet, mrSettings, generators) {
    override fun getMRClassModifiers(): Array<KModifier> = arrayOf(KModifier.ACTUAL)

    override fun apply(generationTask: Task, project: Project) {
        project.tasks.matching {
            it.name.contains("jvm", ignoreCase = true) &&
                (it.name.contains("compile", true) || it.name.contains("processResources", true))
        }.configureEach { it.dependsOn(generationTask) }
    }
}
