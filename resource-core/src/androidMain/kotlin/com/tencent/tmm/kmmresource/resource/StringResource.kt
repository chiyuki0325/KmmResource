/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.tencent.tmm.kmmresource.resource

import android.content.Context
import com.tencent.tmm.kmmresource.resource.desc.Resource
import com.tencent.tmm.kmmresource.resource.desc.ResourceFormatted
import com.tencent.tmm.kmmresource.resource.desc.StringDesc
import androidx.annotation.StringRes

actual data class StringResource(
    @StringRes val resourceId: Int
) {
    actual fun getString(): String = StringDesc.Resource(this).resolve()
    actual fun getString(vararg args: Any): String =
        StringDesc.ResourceFormatted(this, args.toList()).resolve()

    fun getString(context: Context): String = context.getString(resourceId)
}
