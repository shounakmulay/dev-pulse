package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui.model

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class ReaderVisitReadStateTest {
    @Test
    fun `Given a new visit When body becomes visible Then automatic reading is consumed once`() {
        val visit = ReaderVisitReadState()
        assertEquals(ReaderVisitReadDecision.Pending, visit.decision)

        val consumed = visit.consume()

        assertEquals(ReaderVisitReadDecision.Consumed, consumed.decision)
        assertEquals(consumed, consumed.consume())
    }

    @Test
    fun `Given a visit When user changes read status Then automatic reading stays suppressed`() {
        val suppressed = ReaderVisitReadState().suppress()

        assertEquals(ReaderVisitReadDecision.SuppressedByUser, suppressed.decision)
        assertEquals(suppressed, suppressed.suppress())
    }

    @Test
    fun `Given a consumed or suppressed visit When restored Then automatic reading remains ineligible`() {
        for (decision in listOf(ReaderVisitReadDecision.Consumed, ReaderVisitReadDecision.SuppressedByUser)) {
            val saved = ReaderVisitReadState(decision)
            val encoded = Json.encodeToString(ReaderVisitReadState.serializer(), saved)

            val restored = Json.decodeFromString(ReaderVisitReadState.serializer(), encoded)

            assertEquals(decision, restored.decision)
        }
    }
}
