package com.tencent.tmm.kmmresource.resource

import kotlinx.cinterop.DoubleVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.UIKit.UIColor
import platform.UIKit.colorNamed
import kotlin.math.roundToInt

fun ColorResource.getUIColor(): UIColor = requireNotNull(
    UIColor.colorNamed(name = name, inBundle = bundle, compatibleWithTraitCollection = null)
) { "Color resource not found: $name in $bundle" }

@OptIn(ExperimentalForeignApi::class)
internal actual fun ColorResource.readColor(): Int = memScoped {
    val red = alloc<DoubleVar>()
    val green = alloc<DoubleVar>()
    val blue = alloc<DoubleVar>()
    val alpha = alloc<DoubleVar>()
    check(getUIColor().getRed(red.ptr, green.ptr, blue.ptr, alpha.ptr)) {
        "Color resource cannot be converted to sRGB: $name"
    }
    fun channel(value: Double): Int = (value.coerceIn(0.0, 1.0) * 255).roundToInt()
    (channel(alpha.value) shl 24) or (channel(red.value) shl 16) or
        (channel(green.value) shl 8) or channel(blue.value)
}
