/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.tencent.tmm.kmmresource.resource

import platform.Foundation.NSBundle
import com.tencent.tmm.kmmresource.resource.desc.Resource
import com.tencent.tmm.kmmresource.resource.desc.ResourceFormatted
import com.tencent.tmm.kmmresource.resource.desc.StringDesc

actual data class StringResource(
    val resourceId: String,
    val bundle: NSBundle = NSBundle.mainBundle
) {
    actual fun getString(): String = StringDesc.Resource(this).resolve()
    actual fun getString(vararg args: Any): String =
        StringDesc.ResourceFormatted(this, args.toList()).resolve()
}
