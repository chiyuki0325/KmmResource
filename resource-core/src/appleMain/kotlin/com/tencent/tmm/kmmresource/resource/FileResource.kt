/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.tencent.tmm.kmmresource.resource

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSBundle
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.NSURL
import platform.Foundation.create
import platform.posix.memcpy

actual open class FileResource(
    val fileName: String,
    val extension: String,
    val bundle: NSBundle = NSBundle.mainBundle,
) {
    open val path: String
        get() = bundle.pathForResource(
            name = fileName,
            ofType = extension,
            inDirectory = "files"
        )!!
    open val url: NSURL
        get() = bundle.URLForResource(
            name = fileName,
            withExtension = extension,
            subdirectory = "files"
        )!!

    @OptIn(ExperimentalForeignApi::class, kotlinx.cinterop.BetaInteropApi::class)
    actual open fun readBytes(): ByteArray {
        val data = NSData.create(contentsOfFile = path)
            ?: throw ReadFileTextException(this, "Unable to read $path")
        return ByteArray(data.length.toInt()).also { bytes ->
            if (bytes.isNotEmpty()) bytes.usePinned { memcpy(it.addressOf(0), data.bytes, data.length) }
        }
    }

    actual open fun readText(): String = readBytes().decodeToString()
}

@OptIn(ExperimentalForeignApi::class)
internal expect fun readContentOfFile(
    filePath: String,
    error: kotlinx.cinterop.CPointer<ObjCObjectVar<NSError?>>?,
): String?

class ReadFileTextException(
    val fileResource: FileResource,
    val info: String
) : Exception("can't read file $fileResource text ($info)") {
    constructor(fileResource: FileResource, error: NSError) : this(fileResource, error.localizedDescription)
}
