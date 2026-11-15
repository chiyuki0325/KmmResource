package com.tencent.tmm.kmmgradle.tasks

import com.tencent.tmm.kmmgradle.utils.withMutableKlibContents
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile
import java.io.File

open class PackageKLibResourcesTask : DefaultTask() {

    @get:Internal
    internal lateinit var resourcesDir: File

    @get:Internal
    internal lateinit var compileTask: KotlinNativeCompile

    @TaskAction
    fun doAction() {
        if (resourcesDir.exists().not()) {
            return
        }
        val klibFile = compileTask.outputFile.get()
        withMutableKlibContents(klibFile) { klibDirectory ->
            val resRepackDir = File(klibDirectory, "default/resources")
            if (resRepackDir.exists().not()) return@withMutableKlibContents
            resourcesDir.copyRecursively(
                File(resRepackDir, "tmm-resources-ohos"),
                overwrite = true
            )
        }
    }

    companion object {
        const val TASK_NAME = "packageKLibResources"
    }
}
