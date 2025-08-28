package io

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SourceLoaderTest {
    @Test fun normalizesNewlinesAndBom() {
        val raw = "\uFEFFsay cobblestone\r\nsay cobblestone\r"
        val s = Source.fromRaw("x", raw)

        assertEquals(3, s.lineCount)
        assertTrue(s.lines().all { "\r" !in it })
    }
}