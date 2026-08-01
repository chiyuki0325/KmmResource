package com.tencent.tmm.kmmresource.resource

import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

internal object JvmResources {
    private val loader = Thread.currentThread().contextClassLoader ?: JvmResources::class.java.classLoader
    fun stream(path: String): InputStream = loader.getResourceAsStream(path.removePrefix("/"))
        ?: error("Resource not found: $path")
    fun bytes(path: String): ByteArray = stream(path).use { it.readBytes() }
    fun text(path: String): String = bytes(path).decodeToString()

    private val strings by lazy { parseValues("MR/base/strings.xml", "string") }
    private val colors by lazy { parseValues("MR/colors/colors.xml", "color") }

    fun string(name: String, vararg args: Any): String {
        val value = strings[name] ?: strings.entries.firstOrNull { name.endsWith(it.key) }?.value
            ?: error("String resource not found: $name")
        return if (args.isEmpty()) value else String.format(value.replace(Regex("%\\d+\\$"), "%"), *args)
    }

    fun plural(name: String, quantity: Int): String {
        val document = stream("MR/base/plurals.xml").use { DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(it) }
        val nodes = document.getElementsByTagName("plurals")
        for (index in 0 until nodes.length) {
            val node = nodes.item(index)
            if (node.attributes.getNamedItem("name")?.nodeValue?.let(name::endsWith) == true) {
                val children = node.childNodes
                val wanted = if (quantity == 1) "one" else "other"
                for (childIndex in 0 until children.length) {
                    val child = children.item(childIndex)
                    if (child.attributes?.getNamedItem("quantity")?.nodeValue == wanted) {
                        return String.format(child.textContent.androidStringValue().replace(Regex("%\\d+\\$"), "%"), quantity)
                    }
                }
            }
        }
        error("Plural resource not found: $name")
    }

    fun color(name: String): Int {
        val value = (colors[name] ?: colors.entries.firstOrNull { name.endsWith(it.key) }?.value)
            ?.removePrefix("#") ?: error("Color resource not found: $name")
        return when (value.length) {
            6 -> (0xff000000L or value.toLong(16)).toInt()
            8 -> value.toLong(16).toInt()
            else -> error("Unsupported color: $value")
        }
    }

    private fun parseValues(path: String, element: String): Map<String, String> = runCatching {
        stream(path).use { input ->
            val nodes = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input).getElementsByTagName(element)
            buildMap {
                for (index in 0 until nodes.length) {
                    val node = nodes.item(index)
                    put(node.attributes.getNamedItem("name").nodeValue, node.textContent.androidStringValue())
                }
            }
        }
    }.getOrDefault(emptyMap())
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
