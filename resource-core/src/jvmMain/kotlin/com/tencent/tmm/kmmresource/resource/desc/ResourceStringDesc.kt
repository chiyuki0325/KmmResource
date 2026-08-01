/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.tencent.tmm.kmmresource.resource.desc

import com.tencent.tmm.kmmresource.resource.StringResource

actual data class ResourceStringDesc actual constructor(
    val stringRes: StringResource
) : StringDesc {
    override fun toString(): String =
        stringRes.getString()
}
