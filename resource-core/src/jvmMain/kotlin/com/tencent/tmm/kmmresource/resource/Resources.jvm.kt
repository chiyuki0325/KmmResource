package com.tencent.tmm.kmmresource.resource

actual data class StringResource(val resourceName: String) {
    actual fun getString(): String = JvmResources.string(resourceName)
    actual fun getString(vararg args: Any): String = JvmResources.string(resourceName, *args)
}
actual data class PluralsResource(val resourceName: String) {
    actual fun getQuantityString(quantity: Int): String = JvmResources.plural(resourceName, quantity)
    actual fun getQuantityString(quantity: Int, vararg args: Any): String =
        JvmResources.format(getQuantityString(quantity), args)
}
actual data class ColorResource(val resourceName: String) {
    actual fun getColor(): Int = JvmResources.color(resourceName)
}
actual data class ImageResource(val drawableName: String, val suffix: String) {
    fun readBytes(): ByteArray {
        val paths = listOf("xxxldpi", "xxldpi", "xldpi", "ldpi", "mdpi", "sdpi", "base")
            .map { "$it/media/$drawableName.$suffix" }
        return paths.firstNotNullOfOrNull { runCatching { JvmResources.bytes(it) }.getOrNull() }
            ?: error("Image resource not found: $drawableName.$suffix")
    }
}
actual data class FontResource(val fontResourcePath: String) { fun readBytes(): ByteArray = JvmResources.bytes(fontResourcePath) }
actual data class FileResource(val rawResName: String) {
    actual fun readBytes(): ByteArray = JvmResources.bytes(rawResName)
    actual fun readText(): String = readBytes().decodeToString()
}
actual data class AssetResource(val path: String) {
    actual val originalPath: String get() = path
    actual fun readBytes(): ByteArray = JvmResources.bytes(path)
    actual fun readText(): String = readBytes().decodeToString()
}
actual class ResourcePlatformInfo
actual class ResourcePlatformDetails
