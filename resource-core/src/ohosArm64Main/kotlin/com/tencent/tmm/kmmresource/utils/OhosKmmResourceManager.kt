package com.tencent.tmm.kmmresource.utils

import com.tencent.tmm.kmmresource.utils.cache.FontCache
import com.tencent.tmm.knoi.type.ArrayBuffer
import com.tencent.tmm.kmmresource.resource.desc.StringDesc

object OhosKmmResourceManager {

    private val fontCache = FontCache()

    internal val resourceService = getOhosResourceServiceApi()

    fun getString(resName: String): String? {
        return resourceService.getLocalizedString(resName, StringDesc.localeType.locale, emptyArray())
    }

    fun getString(resName: String, vararg args: Any): String? {
        return resourceService.getLocalizedString(resName, StringDesc.localeType.locale, resolveArgs(args))
    }


    fun getPlural(resName: String, args: Int): String? {
        return resourceService.getLocalizedPlural(resName, args, StringDesc.localeType.locale, emptyArray(), false)
    }

    fun getFormattedPlural(resName: String, quantity: Int, args: Array<out Any>): String =
        resourceService.getLocalizedPlural(
            resName, quantity, StringDesc.localeType.locale, resolveArgs(args), true
        ) ?: error("Plural resource not found: $resName")

    private fun resolveArgs(args: Array<out Any>): Array<Any> =
        args.map { (it as? StringDesc)?.resolve() ?: it }.toTypedArray()

    @Deprecated(message = "please use getImageNative")
    fun getImage(resName: String): ArrayBuffer? {
        val resResult = resourceService.getImage(resName)
        return resResult
    }

    fun getImageNative(resName: String): ByteArray? {
        val resResult = processMediaInfo(resName)
        return resResult
    }

    fun getColor(resName: String): Int = getColorByName(resName)

    fun getImageBase64(resName: String): String? {
        val resResult = try {
            getImageBase64ByName(resName)
        } catch (e: Exception) {
            throw e
        }
        return resResult
    }

    fun getFont(resName: String): ByteArray? = fontCache.getResourceByCache(resName)

    fun getFile(resName: String): ArrayBuffer? {
        val resResult = resourceService.getFile(resName)
        return resResult
    }

}
