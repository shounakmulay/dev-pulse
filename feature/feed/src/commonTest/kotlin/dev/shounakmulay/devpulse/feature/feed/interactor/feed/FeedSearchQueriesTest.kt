package dev.shounakmulay.devpulse.feature.feed.interactor.feed

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class FeedSearchQueriesTest {
    @Test
    fun `Given rapid valid queries When debounce expires Then only latest query is emitted`() = runTest {
        val rawQueries = MutableStateFlow("")
        val observed = mutableListOf<String>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            rawQueries.debouncedFeedSearchQueries().toList(observed)
        }
        runCurrent()

        rawQueries.value = "Kotlin"
        advanceTimeBy(299)
        assertEquals(listOf(""), observed)

        rawQueries.value = "Compose"
        advanceTimeBy(299)
        assertEquals(listOf(""), observed)
        advanceTimeBy(1)
        runCurrent()
        assertEquals(listOf("", "Compose"), observed)
    }

    @Test
    fun `Given padded query When trimming or clearing Then duplicates are suppressed and clear is immediate`() = runTest {
        val rawQueries = MutableStateFlow("")
        val observed = mutableListOf<String>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            rawQueries.debouncedFeedSearchQueries().toList(observed)
        }
        runCurrent()

        rawQueries.value = "  Kotlin  "
        advanceTimeBy(300)
        runCurrent()
        assertEquals(listOf("", "Kotlin"), observed)

        rawQueries.value = "Kotlin"
        advanceTimeBy(300)
        runCurrent()
        assertEquals(listOf("", "Kotlin"), observed)

        rawQueries.value = "  "
        runCurrent()
        assertEquals(listOf("", "Kotlin", ""), observed)
    }

    @Test
    fun `Given pending valid query When shortened Then short query immediately replaces it`() = runTest {
        val rawQueries = MutableStateFlow("")
        val observed = mutableListOf<String>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            rawQueries.debouncedFeedSearchQueries().toList(observed)
        }
        runCurrent()

        rawQueries.value = "Kotlin"
        advanceTimeBy(100)
        rawQueries.value = "Ko"
        runCurrent()
        advanceTimeBy(300)
        runCurrent()

        assertEquals(listOf("", "Ko"), observed)
    }
}
