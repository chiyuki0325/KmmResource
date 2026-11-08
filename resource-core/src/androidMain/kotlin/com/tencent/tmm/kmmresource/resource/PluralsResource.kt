/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.tencent.tmm.kmmresource.resource

import android.content.Context
import com.tencent.tmm.kmmresource.KmmResource
import com.tencent.tmm.kmmresource.resource.desc.Plural
import com.tencent.tmm.kmmresource.resource.desc.StringDesc
import com.tencent.tmm.kmmresource.resource.desc.Utils
import androidx.annotation.PluralsRes

actual class PluralsResource(
    @PluralsRes val resourceId: Int
) {

    actual fun getQuantityString(quantity: Int): String = StringDesc.Plural(this, quantity).resolve()

    actual fun getQuantityString(quantity: Int, vararg args: Any): String {
        val context = KmmResource.context
        return Utils.resourcesForContext(context).getQuantityString(
            resourceId, quantity, *Utils.processArgs(args.toList(), context)
        )
    }

    fun getQuantityString(context: Context, number: Int): String =
        context.resources.getQuantityString(resourceId, number)
}
