/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.tencent.tmm.kmmresource.resource

import platform.Foundation.NSBundle
import com.tencent.tmm.kmmresource.internal.Utils
import com.tencent.tmm.kmmresource.resource.desc.Plural
import com.tencent.tmm.kmmresource.resource.desc.StringDesc

actual class PluralsResource(
    val resourceId: String,
    val bundle: NSBundle = NSBundle.mainBundle
) {
    actual fun getQuantityString(quantity: Int): String = StringDesc.Plural(this, quantity).resolve()
    actual fun getQuantityString(quantity: Int, vararg args: Any): String =
        Utils.stringWithFormat(getQuantityString(quantity), Utils.processArgs(args.toList()))
}
