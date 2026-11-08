package com.tencent.tmm.kmmresource.resource

expect class AssetResource {
    val originalPath: String
    fun readBytes(): ByteArray
    fun readText(): String
}
