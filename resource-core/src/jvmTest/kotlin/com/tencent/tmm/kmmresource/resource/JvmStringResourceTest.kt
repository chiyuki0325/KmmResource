package com.tencent.tmm.kmmresource.resource

import com.tencent.tmm.kmmresource.resource.desc.StringDesc
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class JvmStringResourceTest {
    private val previousLocale = StringDesc.localeType

    @BeforeTest
    fun useEnglishResources() {
        StringDesc.localeType = StringDesc.LocaleType.Custom("en")
    }

    @AfterTest
    fun restoreLocale() {
        StringDesc.localeType = previousLocale
    }

    @Test
    fun searchesGeneratedRawfileFallback() {
        assertEquals(
            listOf("fonts/example.ttf", "rawfile/fonts/example.ttf"),
            resourceCandidates("/fonts/example.ttf"),
        )
    }

    @Test
    fun decodesAndroidEscapesAndFormattingWhitespace() {
        val source = """
            first\n
            \n
            second\t\u4e2d\\end
        """.trimIndent()

        assertEquals("first\n \n second\t中\\end", source.androidStringValue())
    }

    @Test
    fun stringsResolveCompleteKeysAndPreserveArgumentPositions() {
        assertEquals("Settings", StringResource("sample_settings").getString())
        assertEquals("Account settings", StringResource("sample_account_settings").getString())
        assertEquals("Clashes with Class A; Class A remains", StringResource("sample_conflict_save").getString("Class A"))
        assertEquals("Room B: Class A", StringResource("sample_reordered").getString("Class A", "Room B"))
        assertFailsWith<IllegalStateException> { StringResource("sample_missing_settings").getString() }
        assertFailsWith<IllegalStateException> { StringResource("settings").getString() }
    }

    @Test
    fun pluralsAndColorsDoNotMatchAShorterSuffix() {
        assertEquals("2 pending items", PluralsResource("sample_pending_items").getQuantityString(2, 2))
        assertEquals("1 pending item", PluralsResource("sample_pending_items").getQuantityString(1, 1))
        assertEquals("1 item", PluralsResource("sample_other_only").getQuantityString(1, 1))
        assertEquals(0xff00aa00.toInt(), ColorResource("sample_notice_color").getColor())
        assertEquals(0x44112233, ColorResource("sample_translucent_color").getColor())
        assertFailsWith<IllegalStateException> { PluralsResource("sample_missing_items").getQuantityString(2) }
        assertFailsWith<IllegalStateException> { ColorResource("sample_missing_color").getColor() }
    }
}
