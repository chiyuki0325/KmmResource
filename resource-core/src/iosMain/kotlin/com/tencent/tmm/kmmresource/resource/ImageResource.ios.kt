package com.tencent.tmm.kmmresource.resource

import platform.Foundation.NSBundle

actual data class ImageResource(
    val assetImageName: String,
    val bundle: NSBundle = NSBundle.mainBundle,
    val suffix: String
) {
    fun filePathForScale(scale: Double): String? {
        val preferredDirectory = when {
            scale >= 3.5 -> XXX_DPI_PATH
            scale >= 2.5 -> XX_DPI_PATH
            scale >= 1.75 -> X_DPI_PATH
            scale >= 1.25 -> L_DPI_PATH
            scale >= 0.875 -> M_DPI_PATH
            else -> S_DPI_PATH
        }
        val fallbackDirectories = listOf(
            preferredDirectory,
            M_DPI_PATH,
            X_DPI_PATH,
            XX_DPI_PATH,
            L_DPI_PATH,
            S_DPI_PATH,
            XXX_DPI_PATH,
            BASE_DPI_PATH,
        )

        return fallbackDirectories.distinct().firstNotNullOfOrNull { directory ->
            bundle.pathForResource(
                name = assetImageName,
                ofType = suffix,
                inDirectory = directory,
            )
        }
    }

    companion object {
        const val S_DPI_PATH = "images/sdpi"
        const val M_DPI_PATH = "images/mdpi"
        const val L_DPI_PATH = "images/ldpi"
        const val X_DPI_PATH = "images/xldpi"
        const val XX_DPI_PATH = "images/xxldpi"
        const val XXX_DPI_PATH = "images/xxxldpi"
        const val BASE_DPI_PATH = "images/base"
    }
}
