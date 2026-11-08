package com.tencent.tmm.kmmresource.resource

import java.io.InputStream
import com.ibm.icu.text.PluralRules
import com.tencent.tmm.kmmresource.resource.desc.StringDesc

internal object JvmResources {
    private val loader = Thread.currentThread().contextClassLoader ?: JvmResources::class.java.classLoader
    fun stream(path: String): InputStream = resourceCandidates(path)
        .firstNotNullOfOrNull(loader::getResourceAsStream)
        ?: error("Resource not found: $path")
    fun bytes(path: String): ByteArray = stream(path).use { it.readBytes() }
    fun text(path: String): String = bytes(path).decodeToString()

    private val tables = java.util.concurrent.ConcurrentHashMap<String, JvmResourceTables>()

    private fun tables(): JvmResourceTables {
        val locale = StringDesc.localeType.locale
        return tables.getOrPut(locale.toLanguageTag()) { JvmResourceTables(loader, locale) }
    }

    fun string(name: String, vararg args: Any): String {
        val value = tables().string(name)
        return if (args.isEmpty()) value else format(value, args)
    }

    fun format(value: String, args: Array<out Any>): String =
        String.format(
            StringDesc.localeType.locale, value,
            *args.map { (it as? StringDesc)?.resolve() ?: it }.toTypedArray()
        )

    fun plural(name: String, quantity: Int): String {
        val values = tables().plural(name)
        val wanted = PluralRules.forLocale(StringDesc.localeType.locale).select(quantity.toDouble())
        val value = values[wanted] ?: values["other"]
            ?: error("Plural resource has no '$wanted' or 'other' value: $name")
        return value
    }

    fun color(name: String): Int {
        val value = tables().color(name).removePrefix("#")
        return when (value.length) {
            6 -> (0xff000000L or value.toLong(16)).toInt()
            8 -> value.toLong(16).toInt()
            else -> error("Unsupported color: $value")
        }
    }

}

internal fun resourceCandidates(path: String): List<String> {
    val normalized = path.removePrefix("/")
    return listOf(normalized, "rawfile/$normalized").distinct()
}

/** Decode Android XML string escapes on JVM, where no aapt resource compiler is present. */
internal fun String.androidStringValue(): String {
    val normalized = replace(Regex("\\s*\n\\s*"), " ").trim()
    return buildString(normalized.length) {
        var index = 0
        while (index < normalized.length) {
            val current = normalized[index]
            if (current != '\\' || index == normalized.lastIndex) {
                append(current)
                index++
                continue
            }
            when (val escaped = normalized[index + 1]) {
                'n' -> append('\n')
                'r' -> append('\r')
                't' -> append('\t')
                '\\' -> append('\\')
                '\'' -> append('\'')
                '"' -> append('"')
                'u' -> {
                    val end = index + 6
                    val code = normalized.substring(index + 2, end.coerceAtMost(normalized.length))
                    if (end <= normalized.length && code.length == 4) {
                        code.toIntOrNull(16)?.let { append(it.toChar()) } ?: append("\\u$code")
                        index = end
                        continue
                    } else append("\\u")
                }
                else -> append(escaped)
            }
            index += 2
        }
    }
}
