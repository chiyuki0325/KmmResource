package com.tencent.tmm.kmmgradle.tasks

import com.tencent.tmm.kmmgradle.utils.copyDirectory
import com.tencent.tmm.kmmgradle.utils.klibs
import com.tencent.tmm.kmmgradle.utils.withKlibResources
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeLink
import java.io.File

open class MergeAppleResourcesTask : DefaultTask() {

    @get:Internal
    internal lateinit var resourcesDir: File

    @get:Internal
    internal lateinit var linkTask: KotlinNativeLink

    @TaskAction
    fun doAction() {
        if (linkTask == null) {
            return
        }
        if (resourcesDir.exists().not()) {
            return
        }
        val mergeResDir = File(resourcesDir.parentFile, MERGE_RES_DIR).apply {
            if (exists().not()) {
                mkdirs()
            }
        }
        linkTask.klibs.forEach { dependency ->
            copyResourcesFromLibraries(
                inputFile = dependency,
                project = project,
                outputDir = mergeResDir
            )
        }
    }

    private fun copyResourcesFromLibraries(
        inputFile: File,
        project: Project,
        outputDir: File
    ) {
        if (inputFile.extension != "klib") return
        if (inputFile.exists().not()) return

        project.logger.info("copy resources from $inputFile into $outputDir")

        withKlibResources(inputFile) { resourcesDir ->
            val resDir = File(resourcesDir, "tmm-resources-apple")
            if (resDir.exists()) {
                resDir.copyDirectory(outputDir)
            }
        }
    }

    companion object {
        const val MERGE_RES_DIR = "mergeRes"
        const val TASK_NAME = "mergeAppleResources"
    }

}
