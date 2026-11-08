/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.tencent.tmm.kmmresource.resource

import com.tencent.tmm.kmmresource.utils.OhosKmmResourceManager

/**
 * complete
 */
actual class PluralsResource(
    val resourceName: String
) {
    actual fun getQuantityString(quantity: Int): String =
        OhosKmmResourceManager.getPlural(resourceName, quantity)
            ?: error("Plural resource not found: $resourceName")

    actual fun getQuantityString(quantity: Int, vararg args: Any): String =
        OhosKmmResourceManager.getFormattedPlural(resourceName, quantity, args)
}
