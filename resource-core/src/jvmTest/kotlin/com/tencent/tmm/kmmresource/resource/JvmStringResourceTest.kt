package com.tencent.tmm.kmmresource.resource

import kotlin.test.Test
import kotlin.test.assertEquals

class JvmStringResourceTest {
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
}
