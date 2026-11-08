/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.tencent.tmm.kmmresource.resource

import android.content.Context
import com.tencent.tmm.kmmresource.KmmResource
import android.content.res.Resources
import androidx.annotation.RawRes
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

actual class FileResource(
    @RawRes
    val rawResId: Int
) {
    actual fun readBytes(): ByteArray =
        KmmResource.context.resources.openRawResource(rawResId).use { it.readBytes() }

    actual fun readText(): String = readBytes().decodeToString()

    fun readText(context: Context): String {
        val resources: Resources = context.resources
        val inputStream: InputStream = resources.openRawResource(rawResId)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        return bufferedReader.use { it.readText() }
    }
}
