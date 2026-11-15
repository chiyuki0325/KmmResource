/*
 * Copyright 2023 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.tencent.tmm.kmmgradle.utils

import org.jetbrains.kotlin.konan.file.zipDirAs
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipFile

internal fun unzipTo(outputDirectory: File, zipFile: File) {
    ZipFile(zipFile).use { zip ->
        val outputDirectoryCanonicalPath = outputDirectory.canonicalPath
        for (entry in zip.entries()) {
            unzipEntryTo(outputDirectory, outputDirectoryCanonicalPath, zip, entry)
        }
    }
}

private fun unzipEntryTo(
    outputDirectory: File,
    outputDirectoryCanonicalPath: String,
    zip: ZipFile,
    entry: ZipEntry
) {
    val output = outputDirectory.resolve(entry.name)
    if (!output.canonicalPath.startsWith(outputDirectoryCanonicalPath)) {
        throw ZipException("Zip entry '${entry.name}' is outside of the output directory")
    }
    if (entry.isDirectory) {
        output.mkdirs()
    } else {
        output.parentFile.mkdirs()
        zip.getInputStream(entry).use { it.copyTo(output) }
    }
}

private fun InputStream.copyTo(file: File): Long =
    file.outputStream().use { copyTo(it) }

internal fun <T> withKlibResources(klibFile: File, action: (File) -> T): T {
    if (klibFile.isDirectory) {
        return action(klibFile.resolve("default/resources"))
    }

    val extractionDir = Files.createTempDirectory("kmm-resource-klib-").toFile()
    return try {
        unzipTo(outputDirectory = extractionDir, zipFile = klibFile)
        action(extractionDir.resolve("default/resources"))
    } finally {
        extractionDir.deleteRecursively()
    }
}

internal fun <T> withMutableKlibContents(klibFile: File, action: (File) -> T): T {
    if (klibFile.isDirectory) {
        return action(klibFile)
    }

    val parentDirectory = requireNotNull(klibFile.parentFile) {
        "KLIB file has no parent directory: $klibFile"
    }
    val extractionDir = Files.createTempDirectory(
        parentDirectory.toPath(),
        "kmm-resource-klib-",
    ).toFile()
    val replacementFile = Files.createTempFile(
        parentDirectory.toPath(),
        "kmm-resource-klib-",
        ".klib",
    ).toFile().apply { delete() }

    return try {
        unzipTo(outputDirectory = extractionDir, zipFile = klibFile)
        val result = action(extractionDir)
        org.jetbrains.kotlin.konan.file.File(extractionDir.path).zipDirAs(
            org.jetbrains.kotlin.konan.file.File(replacementFile.path),
        )
        Files.move(replacementFile.toPath(), klibFile.toPath(), REPLACE_EXISTING)
        result
    } finally {
        extractionDir.deleteRecursively()
        replacementFile.delete()
    }
}
