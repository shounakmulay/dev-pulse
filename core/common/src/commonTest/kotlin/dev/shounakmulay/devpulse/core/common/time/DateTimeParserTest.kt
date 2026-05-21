package dev.shounakmulay.devpulse.core.common.time

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DateTimeParserTest {

    private val parser = DateTimeParser()

    @Test
    fun `Given RFC 1123 date with GMT When parsed Then epoch milliseconds are returned`() {
        val result = parser.parseEpochMilliseconds("Tue, 19 May 2026 10:00:00 GMT")

        assertEquals(1779184800000L, result)
    }

    @Test
    fun `Given date with numeric offset When parsed Then epoch milliseconds are returned`() {
        val result = parser.parseEpochMilliseconds("Tue, 19 May 2026 10:00:00 +0530")

        assertEquals(1779165000000L, result)
    }

    @Test
    fun `Given date without weekday When parsed Then epoch milliseconds are returned`() {
        val result = parser.parseEpochMilliseconds("19 May 2026 10:00:00 GMT")

        assertEquals(1779184800000L, result)
    }

    @Test
    fun `Given ISO date When parsed Then epoch milliseconds are returned`() {
        val result = parser.parseEpochMilliseconds("2026-05-19T10:00:00Z")

        assertEquals(1779184800000L, result)
    }

    @Test
    fun `Given blank null or invalid date When parsed Then null is returned`() {
        assertNull(parser.parseEpochMilliseconds(null))
        assertNull(parser.parseEpochMilliseconds("   "))
        assertNull(parser.parseEpochMilliseconds("not a date"))
    }
}
