package dev.shounakmulay.devpulse.core.common.coroutines

import kotlinx.coroutines.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

class SafeRunCatchingTest {
    @Test
    fun `Given a successful block When caught safely Then it executes once and returns its value`() {
        var executions = 0

        val result = safeRunCatching {
            executions += 1
            "completed"
        }

        assertEquals(1, executions)
        assertEquals("completed", result.getOrThrow())
    }

    @Test
    fun `Given an ordinary exception When caught safely Then failure preserves the exception`() {
        val exception = IllegalStateException("failed")

        val result = safeRunCatching<String> { throw exception }

        assertTrue(result.isFailure)
        assertSame(exception, result.exceptionOrNull())
    }

    @Test
    fun `Given a cancellation exception When caught safely Then it is thrown unchanged`() {
        val exception = CancellationException("cancelled")

        val thrown = assertFailsWith<CancellationException> {
            safeRunCatching<String> { throw exception }
        }

        assertSame(exception, thrown)
    }
}
