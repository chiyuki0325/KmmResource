/*
 * Copyright 2023 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.tencent.tmm.kmmresource.resource

import android.content.Context
import com.tencent.tmm.kmmresource.KmmResource
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

actual class ColorResource(
    @ColorRes val resourceId: Int
) {
    actual fun getColor(): Int = getColor(KmmResource.context)
    fun getColor(context: Context): Int = ContextCompat.getColor(context, resourceId)
}
