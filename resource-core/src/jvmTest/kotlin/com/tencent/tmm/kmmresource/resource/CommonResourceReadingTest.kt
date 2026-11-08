package com.tencent.tmm.kmmresource.resource

import com.tencent.tmm.kmmresource.resource.desc.StringDesc
import com.tencent.tmm.kmmresource.resource.desc.desc
import com.tencent.tmm.kmmresource.resource.desc.plus
import java.util.Locale
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CommonResourceReadingTest {
    private val previousLocale = StringDesc.localeType
    private val previousSystemLocale = Locale.getDefault()

    @AfterTest
    fun restoreLocale() {
        StringDesc.localeType = previousLocale
        Locale.setDefault(previousSystemLocale)
    }

    @Test
    fun localeChangesApplyToExistingResourcesWithBaseFallback() {
        val resource = StringResource("sample_settings")
        StringDesc.localeType = StringDesc.LocaleType.Custom("zh-CN")
        assertEquals("设置", resource.getString())
        assertEquals("Account settings", StringResource("sample_account_settings").getString())
        StringDesc.localeType = StringDesc.LocaleType.Custom("en")
        assertEquals("Settings", resource.getString())
        Locale.setDefault(Locale.SIMPLIFIED_CHINESE)
        StringDesc.localeType = StringDesc.LocaleType.System
        assertEquals("设置", resource.getString())
    }

    @Test
    fun nestedDescriptionsResolveBeforeFormatting() {
        StringDesc.localeType = StringDesc.LocaleType.Custom("zh")
        val description = StringResource("sample_settings").desc() + "!".desc()
        assertEquals("设置!", description.resolve())
        assertEquals("Room: 设置!", StringResource("sample_reordered").getString(description, "Room"))
    }

    @Test
    fun pluralSelectionDoesNotConsumeFormatArguments() {
        StringDesc.localeType = StringDesc.LocaleType.Custom("en")
        val resource = PluralsResource("sample_pending_items")
        assertEquals("%1\$d pending items", resource.getQuantityString(2))
        assertEquals("7 pending items", resource.getQuantityString(2, 7))
        assertEquals("2 pending items", resource.desc(2).resolve())
        StringDesc.localeType = StringDesc.LocaleType.Custom("ru")
        val russian = PluralsResource("sample_items")
        assertEquals("2 предмета", russian.getQuantityString(2, 2))
        assertEquals("5 предметов", russian.getQuantityString(5, 5))
        assertEquals("21 предмет", russian.getQuantityString(21, 21))
        StringDesc.localeType = StringDesc.LocaleType.Custom("zh")
        assertEquals("%1\$d items", russian.getQuantityString(1))
    }

    @Test
    fun fileAndAssetReadersUseUtf8AndFailOnMissingResources() {
        val file = FileResource("reading.txt")
        val asset = AssetResource("reading.txt")
        assertEquals("课程资源\n", file.readText())
        assertEquals(file.readText(), asset.readText())
        assertContentEquals(file.readText().encodeToByteArray(), file.readBytes())
        assertContentEquals(file.readBytes(), asset.readBytes())
        assertFailsWith<IllegalStateException> { FileResource("absent.txt").readBytes() }
        assertFailsWith<IllegalStateException> { AssetResource("absent.txt").readText() }
    }
}
