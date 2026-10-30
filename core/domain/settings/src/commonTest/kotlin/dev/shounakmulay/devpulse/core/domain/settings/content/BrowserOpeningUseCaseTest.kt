package dev.shounakmulay.devpulse.core.domain.settings.content

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.data.settings.ContentSettingsRepository
import dev.shounakmulay.devpulse.core.domain.models.contentSettings.BrowserOpeningMode
import dev.shounakmulay.devpulse.core.domain.models.contentSettings.BrowserOpeningTarget
import dev.shounakmulay.devpulse.core.domain.models.contentSettings.ContentTextSettings
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class BrowserOpeningUseCaseTest {
    private val url = "https://example.com/article"
    private val dispatchers = object : DispatcherProvider {
        override val defaultDispatcher = Dispatchers.Unconfined
        override val ioDispatcher = Dispatchers.Unconfined
    }

    @Test
    fun `Given default preference When opening in browser Then the in-app browser is selected`(): Unit = runBlocking {
        val repository = Repository()
        assertEquals(BrowserOpeningTarget.InApp(url), GetBrowserOpeningTargetUseCase(repository, dispatchers)(url).getOrThrow())
    }

    @Test
    fun `Given browser preference changes When opening a link Then the latest saved choice is used`(): Unit = runBlocking {
        val repository = Repository()
        val set = SetBrowserOpeningModeUseCase(repository, dispatchers)
        val observe = ObserveBrowserOpeningModeUseCase(repository, dispatchers)
        val resolve = GetBrowserOpeningTargetUseCase(repository, dispatchers)
        set(BrowserOpeningMode.EXTERNAL_BROWSER).getOrThrow()
        assertEquals(BrowserOpeningMode.EXTERNAL_BROWSER, observe().first().getOrThrow())
        assertEquals(BrowserOpeningTarget.External(url), resolve(url).getOrThrow())
        set(BrowserOpeningMode.IN_APP_BROWSER).getOrThrow()
        assertEquals(BrowserOpeningTarget.InApp(url), resolve(url).getOrThrow())
    }

    @Test
    fun `Given HTTP or HTTPS links When choosing a browser Then the original URL is preserved`(): Unit = runBlocking {
        val repository = Repository().apply { mode.value = BrowserOpeningMode.EXTERNAL_BROWSER }
        val resolve = GetBrowserOpeningTargetUseCase(repository, dispatchers)
        for (link in listOf("http://example.com/article", "https://example.com/article?x=1#section")) {
            assertEquals(BrowserOpeningTarget.External(link), resolve(link).getOrThrow())
        }
    }

    @Test
    fun `Given read or write failure When using settings Then the original failure is returned`(): Unit = runBlocking {
        val failure = IllegalStateException("disk unavailable")
        val repository = Repository().apply { readFailure = failure; writeFailure = failure }
        assertSame(failure, ObserveBrowserOpeningModeUseCase(repository, dispatchers)().first().exceptionOrNull())
        assertSame(failure, GetBrowserOpeningTargetUseCase(repository, dispatchers)(url).exceptionOrNull())
        assertSame(failure, SetBrowserOpeningModeUseCase(repository, dispatchers)(BrowserOpeningMode.EXTERNAL_BROWSER).exceptionOrNull())
        assertEquals(BrowserOpeningMode.IN_APP_BROWSER, repository.mode.value)
    }

    @Test
    fun `Given observation construction fails When observing Then failure is returned`(): Unit = runBlocking {
        val failure = IllegalStateException("observation unavailable")
        val repository = Repository().apply { constructionFailure = failure }
        assertSame(failure, ObserveBrowserOpeningModeUseCase(repository, dispatchers)().first().exceptionOrNull())
    }

    @Test
    fun `Given cancellation When reading or writing Then cancellation propagates`(): Unit = runBlocking {
        val repository = Repository().apply { readFailure = CancellationException("cancelled"); writeFailure = readFailure }
        assertFailsWith<CancellationException> { ObserveBrowserOpeningModeUseCase(repository, dispatchers)().first() }
        assertFailsWith<CancellationException> { GetBrowserOpeningTargetUseCase(repository, dispatchers)(url) }
        assertFailsWith<CancellationException> { SetBrowserOpeningModeUseCase(repository, dispatchers)(BrowserOpeningMode.EXTERNAL_BROWSER) }
    }

    private class Repository : ContentSettingsRepository {
        val mode = MutableStateFlow(BrowserOpeningMode.IN_APP_BROWSER)
        var readFailure: Exception? = null
        var writeFailure: Exception? = null
        var constructionFailure: Exception? = null
        override fun observeBrowserOpeningMode(defaultMode: BrowserOpeningMode): Flow<BrowserOpeningMode> {
            constructionFailure?.let { throw it }
            return flow {
                readFailure?.let { throw it }
                mode.collect { emit(it) }
            }
        }
        override suspend fun setBrowserOpeningMode(mode: BrowserOpeningMode) {
            writeFailure?.let { throw it }
            this.mode.value = mode
        }
        override fun observeContentTextSettings(defaultSettings: ContentTextSettings): Flow<ContentTextSettings> = error("Unused")
        override suspend fun updateContentTextSettings(settings: ContentTextSettings) = error("Unused")
        override suspend fun setContentTextScale(scale: Float) = error("Unused")
        override suspend fun setContentLineHeightScale(spacing: Float) = error("Unused")
    }
}
