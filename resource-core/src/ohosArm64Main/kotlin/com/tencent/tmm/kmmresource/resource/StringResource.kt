/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.tencent.tmm.kmmresource.resource

import com.tencent.tmm.kmmresource.resource.desc.Resource
import com.tencent.tmm.kmmresource.resource.desc.ResourceFormatted
import com.tencent.tmm.kmmresource.resource.desc.StringDesc

/**
 * complete
 */
actual data class StringResource(
    val resourceName: String
)  {
    actual fun getString(): String = StringDesc.Resource(this).resolve()
    actual fun getString(vararg args: Any): String =
        StringDesc.ResourceFormatted(this, args.toList()).resolve()
}
