/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.tencent.tmm.kmmgradle.generator.apple

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.KModifier
import com.tencent.tmm.kmmgradle.generator.ImagesGenerator
import com.tencent.tmm.kmmgradle.generator.ObjectBodyExtendable
import com.tencent.tmm.kmmgradle.generator.ohos.OhosImagesGenerator.Companion.getImageSuffix
import com.tencent.tmm.kmmgradle.generator.apple.AppleMRGenerator.Companion.ASSETS_DIR_NAME
import com.tencent.tmm.kmmgradle.utils.Constants
import com.tencent.tmm.kmmgradle.utils.svg
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.gradle.api.file.FileTree
import java.io.File

class AppleImagesFilesGenerator(
    inputFileTree: FileTree
) : ImagesGenerator(inputFileTree = inputFileTree), ObjectBodyExtendable by AppleGeneratorHelper() {

    override fun getClassModifiers(): Array<KModifier> = arrayOf(KModifier.ACTUAL)

    override fun getPropertyModifiers(): Array<KModifier> = arrayOf(KModifier.ACTUAL)

    override fun getPropertyInitializer(fileName: String): CodeBlock? {
        return CodeBlock.of(
            "ImageResource(assetImageName = %S, bundle = ${Constants.platformInitializeArgsIOS}, suffix = %S)",
            fileName.substringBefore("."),
            getImageSuffix(fileName)
        )
    }

    override fun generateResources(
        resourcesGenerationDir: File,
        keyFileMap: Map<String, List<File>>
    ) {
        if (keyFileMap.isNullOrEmpty()) {
            return
        }

        keyFileMap.flatMap { (key, files) ->
            files.map { key to it }
        }.forEach { (key, file) ->
            val scale = file.nameWithoutExtension.substringAfter("@").substringBefore("x")
            val scaleName = when (scale) {
                "0.75" -> "sdpi"
                "1" -> "mdpi"
                "1.5" -> "ldpi"
                "2" -> "xldpi"
                "3" -> "xxldpi"
                "4" -> "xxxldpi"
                else -> if (file.svg) {
                    "base"
                } else {
                    throw RuntimeException("$file - unknown scale ($scale). Please check scale is configured. For example: ${scale}@1x.png")
                }
            }

            // 创建资源目录
            val mediaDir = File(resourcesGenerationDir, "images/${scaleName}").apply {
                mkdirs()
            }

            val resourceFile = File(mediaDir, "${key}.${file.extension}")
            file.copyTo(resourceFile)

            if (file.svg) {
                generateSvgAsset(resourcesGenerationDir, key, file)
            }
        }
    }

    private fun generateSvgAsset(resourcesGenerationDir: File, key: String, file: File) {
        val assetDir = File(resourcesGenerationDir, "$ASSETS_DIR_NAME/$key.imageset").apply {
            mkdirs()
        }
        file.copyTo(File(assetDir, file.name), overwrite = true)
        val content = buildJsonObject {
            put(
                "images",
                buildJsonArray {
                    add(
                        buildJsonObject {
                            put("idiom", JsonPrimitive("universal"))
                            put("filename", JsonPrimitive(file.name))
                        }
                    )
                }
            )
            put(
                "info",
                buildJsonObject {
                    put("version", JsonPrimitive(1))
                    put("author", JsonPrimitive("xcode"))
                }
            )
            put(
                "properties",
                buildJsonObject {
                    put("preserves-vector-representation", JsonPrimitive(true))
                }
            )
        }
        File(assetDir, "Contents.json").writeText(content.toString())
    }

    private companion object {
        val VALID_SIZES: IntRange = 1..3
    }
}
