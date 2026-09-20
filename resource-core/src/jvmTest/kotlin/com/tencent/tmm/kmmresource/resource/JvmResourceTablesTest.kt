package com.tencent.tmm.kmmresource.resource

import java.net.URLClassLoader
import java.nio.file.Files
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class JvmResourceTablesTest {
    @Test
    fun dependencyJarsKeepModulesWithTheSameSourceNamesSeparate() {
        val first = mapOf(
            "string" to """{"string":[{"name":"alpha_settings","value":"A"}]}""",
            "plural" to """{"plural":[{"name":"alpha_items","value":[{"quantity":"other","value":"A items"}]}]}""",
            "color" to """{"color":[{"name":"alpha_color","value":"#ff112233"}]}""",
        )
        val second = mapOf(
            "string" to """{"string":[{"name":"beta_settings","value":"B"}]}""",
            "plural" to """{"plural":[{"name":"beta_items","value":[{"quantity":"other","value":"B items"}]}]}""",
            "color" to """{"color":[{"name":"beta_color","value":"#ff445566"}]}""",
        )
        for (modules in listOf(listOf(first, second), listOf(second, first))) {
            withTables(modules) { tables ->
                assertEquals("A", tables.string("alpha_settings"))
                assertEquals("B", tables.string("beta_settings"))
                assertEquals("A items", tables.plural("alpha_items")["other"])
                assertEquals("B items", tables.plural("beta_items")["other"])
                assertEquals("#ff112233", tables.color("alpha_color"))
                assertEquals("#ff445566", tables.color("beta_color"))
            }
        }
    }

    @Test
    fun theSameTableAppearingTwiceOnTheClasspathIsAccepted() {
        val table = mapOf(
            "string" to """{"string":[{"name":"duplicate_settings","value":"Settings"}]}""",
            "plural" to """{"plural":[{"name":"duplicate_items","value":[{"quantity":"other","value":"Items"}]}]}""",
            "color" to """{"color":[{"name":"duplicate_color","value":"#ff112233"}]}""",
        )
        withTables(listOf(table, table)) { tables ->
            assertEquals("Settings", tables.string("duplicate_settings"))
            assertEquals("Items", tables.plural("duplicate_items")["other"])
            assertEquals("#ff112233", tables.color("duplicate_color"))
        }
    }

    @Test
    fun conflictingNamesAcrossModulesFailInsteadOfDependingOnClasspathOrder() {
        val first = """{"string":[{"name":"duplicate_settings","value":"Settings"}]}"""
        val second = """{"string":[{"name":"duplicate_settings","value":"Preferences"}]}"""
        withTables(listOf(mapOf("string" to first), mapOf("string" to second))) { tables ->
            val error = assertFailsWith<IllegalStateException> { tables.string("duplicate_settings") }
            assertTrue(error.message!!.contains("Conflicting string resource 'duplicate_settings'"))
        }
    }

    @Test
    fun aResourceTableDoesNotAcceptUndeclaredSuffixAliases() {
        withTables(listOf(mapOf("string" to """{"string":[{"name":"sample_settings","value":"Settings"}]}"""))) { tables ->
            assertFailsWith<IllegalStateException> { tables.string("settings") }
            assertFailsWith<IllegalStateException> { tables.string("other_sample_settings") }
        }
    }

    @Test
    fun malformedTablesAreNotSilentlyTreatedAsMissingResources() {
        withTables(listOf(mapOf("string" to "not json"))) { tables ->
            assertFailsWith<IllegalArgumentException> { tables.string("settings") }
        }
    }

    private fun withTables(modules: List<Map<String, String>>, block: (JvmResourceTables) -> Unit) {
        val directory = Files.createTempDirectory("kmmresource-tables-").toFile()
        try {
            val jars = modules.mapIndexed { index, tables ->
                directory.resolve("module-$index.jar").also { jar ->
                    JarOutputStream(jar.outputStream()).use { output ->
                        tables.forEach { (kind, json) ->
                            output.putNextEntry(JarEntry("base/element/$kind.json"))
                            output.write(json.toByteArray(Charsets.UTF_8))
                            output.closeEntry()
                        }
                    }
                }.toURI().toURL()
            }
            URLClassLoader(jars.toTypedArray(), null).use { block(JvmResourceTables(it)) }
        } finally {
            directory.deleteRecursively()
        }
    }
}
