/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.tencent.tmm.kmmresource.resource


actual class AssetResource(val path: String) {
    actual fun readBytes(): ByteArray = com.tencent.tmm.kmmresource.utils.processRawFile(path)
        ?: error("Asset resource not found: $path")
    actual fun readText(): String = readBytes().decodeToString()
    actual val originalPath: String
        get() = path
}
