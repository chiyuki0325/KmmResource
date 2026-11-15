package com.tencent.tmm.kmmgradle.tasks.action

import com.tencent.tmm.kmmgradle.utils.withMutableKlibContents
import org.gradle.api.Action
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile
import java.io.File

class PackageKLibResourcesAction(private val resourcesDir: File, private val resToDirName: String) :
    Action<KotlinNativeCompile> {
    override fun execute(compileTask: KotlinNativeCompile) {
        if (resourcesDir.exists().not()) {
            return
        }
        val klibFile = compileTask.outputFile.get()
        withMutableKlibContents(klibFile) { klibDirectory ->
            val resRepackDir = File(klibDirectory, "default/resources")
            if (resRepackDir.exists().not()) return@withMutableKlibContents
            resourcesDir.copyRecursively(
                File(resRepackDir, resToDirName),
                overwrite = true
            )
        }
    }
}
