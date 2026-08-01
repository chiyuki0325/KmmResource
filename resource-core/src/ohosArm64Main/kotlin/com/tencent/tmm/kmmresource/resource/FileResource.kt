/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.tencent.tmm.kmmresource.resource

actual class FileResource(
    val rawResName: String
) {
    fun readBytes(): ByteArray = com.tencent.tmm.kmmresource.utils.processRawFile(rawResName)
        ?: error("File resource not found: $rawResName")
    fun readText(): String = readBytes().decodeToString()
}
