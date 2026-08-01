package com.tencent.tmm.kmmresource.resource

actual data class StringResource(val resourceName: String) {
    fun getString(vararg args: Any): String = JvmResources.string(resourceName, *args)
}
actual data class PluralsResource(val resourceName: String) {
    fun getQuantityString(number: Int): String = JvmResources.plural(resourceName, number)
}
actual data class ColorResource(val resourceName: String) {
    fun getColor(): Int = JvmResources.color(resourceName)
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
    fun readBytes(): ByteArray = JvmResources.bytes(rawResName)
    fun readText(): String = JvmResources.text(rawResName)
}
actual data class AssetResource(val path: String) {
    actual val originalPath: String get() = path
    fun readBytes(): ByteArray = JvmResources.bytes(path)
    fun readText(): String = JvmResources.text(path)
}
actual class ResourcePlatformInfo
actual class ResourcePlatformDetails
