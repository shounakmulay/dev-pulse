package dev.shounakmulay.devpulse.core.domain.models.feed

import kotlin.test.Test
import kotlin.test.assertEquals

class RssFeedQueueOrderTest {

    @Test
    fun `Given queue dimensions When reading global queue order Then domain defines processor sort concepts`() {
        assertEquals(
            listOf(
                RssFeedQueueOrderDimension.REQUESTOR,
                RssFeedQueueOrderDimension.ACTION_TYPE
            ),
            RssFeedQueueOrderDimension.GLOBAL_QUEUE_ORDER
        )
    }
}
