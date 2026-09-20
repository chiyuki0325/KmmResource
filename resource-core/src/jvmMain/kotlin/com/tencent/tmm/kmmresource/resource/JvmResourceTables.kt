package com.tencent.tmm.kmmresource.resource

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.URL

/**
 * MR and these build-generated tables use the same complete, prefixed resource names.
 * The table format is shared with OHOS and was already emitted by the original JVM generator,
 * so previously compiled MR declarations and their packaged tables remain usable together.
 */
internal class JvmResourceTables(private val loader: ClassLoader) {
    private val strings by lazy { load("string") { it.jsonPrimitive.content.androidStringValue() } }
    private val colors by lazy { load("color") { it.jsonPrimitive.content } }
    private val plurals by lazy {
        load("plural") { value ->
            buildMap {
                value.jsonArray.forEach { item ->
                    val entry = item.jsonObject
                    val quantity = entry.getValue("quantity").jsonPrimitive.content
                    check(!containsKey(quantity)) { "Duplicate plural quantity: $quantity" }
                    put(quantity, entry.getValue("value").jsonPrimitive.content.androidStringValue())
                }
            }
        }
    }

    fun string(name: String): String = strings[name] ?: error("String resource not found: $name")
    fun plural(name: String): Map<String, String> = plurals[name] ?: error("Plural resource not found: $name")
    fun color(name: String): String = colors[name] ?: error("Color resource not found: $name")

    private fun <T> load(kind: String, decode: (JsonElement) -> T): Map<String, T> {
        val values = mutableMapOf<String, Pair<JsonElement, URL>>()
        // Enumerate every module's table, including tables packaged in dependency JARs.
        // Never select the first classpath file or infer a module prefix from a suffix.
        val tables = loader.getResources("base/element/$kind.json").asSequence()
            .distinctBy { it.toExternalForm() }
        tables.forEach { table ->
            val entries = table.openStream().bufferedReader(Charsets.UTF_8).use {
                Json.parseToJsonElement(it.readText()).jsonObject.getValue(kind).jsonArray
            }
            entries.forEach { item ->
                val entry = item.jsonObject
                val name = entry.getValue("name").jsonPrimitive.content
                check(name.isNotEmpty()) { "Empty $kind resource name in $table" }
                val value = entry.getValue("value")
                val existing = values[name]
                // The same table reachable twice on the classpath is harmless; only conflicting values are ambiguous.
                if (existing == null) {
                    values[name] = value to table
                } else {
                    check(existing.first == value) {
                        "Conflicting $kind resource '$name' in ${existing.second} and $table"
                    }
                }
            }
        }
        return values.mapValues { decode(it.value.first) }
    }
}
