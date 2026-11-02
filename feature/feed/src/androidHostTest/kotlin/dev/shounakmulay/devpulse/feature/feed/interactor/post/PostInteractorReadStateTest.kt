package dev.shounakmulay.devpulse.feature.feed.interactor.post

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.shounakmulay.devpulse.core.common.coroutines.ApplicationScope
import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.data.feed.repository.FeedRepository
import dev.shounakmulay.devpulse.core.data.feed.repository.PostRepository
import dev.shounakmulay.devpulse.core.data.settings.ContentSettingsRepository
import dev.shounakmulay.devpulse.core.domain.feed.feed.ConvertToMarkdownUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.ExtractInitialsUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.GetPinnedAndRecentFeedsUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.GetRecentFeedItemsUseCase
import dev.shounakmulay.devpulse.core.domain.feed.posts.FetchPostContentUseCase
import dev.shounakmulay.devpulse.core.domain.feed.posts.GetPaginatedFeedPostsUseCase
import dev.shounakmulay.devpulse.core.domain.feed.posts.GetPostContentUseCase
import dev.shounakmulay.devpulse.core.domain.feed.posts.GetPostDetailUseCase
import dev.shounakmulay.devpulse.core.domain.feed.posts.GetRssEncodedContentUseCase
import dev.shounakmulay.devpulse.core.domain.feed.posts.SavePostContentUseCase
import dev.shounakmulay.devpulse.core.domain.feed.posts.SetPostBookmarkedUseCase
import dev.shounakmulay.devpulse.core.domain.feed.posts.SetPostReadUseCase
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.contentSettings.BrowserOpeningMode
import dev.shounakmulay.devpulse.core.domain.models.contentSettings.BrowserOpeningTarget
import dev.shounakmulay.devpulse.core.domain.models.contentSettings.ContentTextSettings
import dev.shounakmulay.devpulse.core.domain.models.feed.OpmlFeedImportData
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeed
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedIdentity
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueEntry
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedSearchResult
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedWithUserState
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPost
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostContent
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostContentType
import dev.shounakmulay.devpulse.core.domain.models.post.RssParsedPostContent
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostQuery
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostWithFeedIdentityAndSearch
import dev.shounakmulay.devpulse.core.domain.models.raw.parsed.ParsedFeed
import dev.shounakmulay.devpulse.core.domain.settings.content.GetBrowserOpeningTargetUseCase
import dev.shounakmulay.devpulse.core.domain.settings.content.SetContentTextSettingFontScaleUseCase
import dev.shounakmulay.devpulse.core.domain.settings.content.SetContentTextSettingLineHeightScaleUseCase
import dev.shounakmulay.devpulse.core.logging.events.DPEvent
import dev.shounakmulay.devpulse.core.ui.datetime.DateTimeStringConverter
import dev.shounakmulay.devpulse.feature.feed.interactor.feed.FeedInteractor
import dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui.PostDetailScreenEvent
import dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui.PostDetailViewModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Instant
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [35])
class PostInteractorReadStateTest {
    @Test
    fun `Given overlapping actions for one post When setting read status Then neither choice is dropped`() = runTest {
        val fixture = Fixture(StandardTestDispatcher(testScheduler))
        val release = CompletableDeferred<Unit>()
        fixture.repository.waitFor = release
        val first = async { fixture.interactor.setPostRead(postId = PostId, isRead = true) }
        val second = async { fixture.interactor.setPostRead(postId = PostId, isRead = false) }
        runCurrent()

        assertEquals(listOf(PostId to true, PostId to false), fixture.repository.commands)
        release.complete(Unit)
        first.await()
        second.await()
    }

    @Test
    fun `Given a failed read write When user repeats the action Then later write is accepted`() = runTest {
        val fixture = Fixture(StandardTestDispatcher(testScheduler))
        fixture.repository.thrown = IllegalStateException("database busy")

        fixture.interactor.setPostRead(postId = PostId, isRead = true)
        fixture.repository.thrown = null
        fixture.interactor.setPostRead(postId = PostId, isRead = true)

        assertEquals(listOf(PostId to true, PostId to true), fixture.repository.commands)
    }

    @Test
    fun `Given cancelled read write When setting read status Then cancellation propagates`() = runTest {
        val fixture = Fixture(StandardTestDispatcher(testScheduler))
        fixture.repository.thrown = CancellationException("screen left")

        assertEquals("screen left", assertFailsWith<CancellationException> {
            fixture.interactor.setPostRead(postId = PostId, isRead = true)
        }.message)
    }

    @Test
    fun `Given default browser preference When opening a post Then the target is in-app`() = runTest {
        val fixture = Fixture(StandardTestDispatcher(testScheduler))

        assertEquals(BrowserOpeningTarget.InApp(ArticleUrl), fixture.interactor.getBrowserOpeningTarget(ArticleUrl))
    }

    @Test
    fun `Given browser preference changes When opening posts Then each action uses the latest choice`() = runTest {
        val fixture = Fixture(StandardTestDispatcher(testScheduler))
        fixture.contentSettings.mode = BrowserOpeningMode.EXTERNAL_BROWSER

        assertEquals(BrowserOpeningTarget.External(ArticleUrl), fixture.interactor.getBrowserOpeningTarget(ArticleUrl))
        fixture.contentSettings.mode = BrowserOpeningMode.IN_APP_BROWSER
        assertEquals(BrowserOpeningTarget.InApp(ArticleUrl), fixture.interactor.getBrowserOpeningTarget(ArticleUrl))
    }

    @Test
    fun `Given browser preference read failure When opening a post Then the original URL targets the external browser`() = runTest {
        val fixture = Fixture(StandardTestDispatcher(testScheduler))
        fixture.contentSettings.thrown = IllegalStateException("settings unavailable")

        assertEquals(BrowserOpeningTarget.External(ArticleUrl), fixture.interactor.getBrowserOpeningTarget(ArticleUrl))
    }

    @Test
    fun `Given cancelled browser preference read When opening a post Then cancellation propagates`() = runTest {
        val fixture = Fixture(StandardTestDispatcher(testScheduler))
        fixture.contentSettings.thrown = CancellationException("screen left")

        assertEquals("screen left", assertFailsWith<CancellationException> {
            fixture.interactor.getBrowserOpeningTarget(ArticleUrl)
        }.message)
    }

    @Test
    fun `Given visible content When availability events overlap Then automatic reading writes once`() = runTest {
        withViewModel { viewModel, fixture ->
            fixture.repository.posts.emit(article())
            viewModel.awaitPost()
            val release = CompletableDeferred<Unit>()
            fixture.repository.waitFor = release

            repeat(5) { viewModel.onEvent(PostDetailScreenEvent.ContentAvailable) }
            viewModel.flushEvents()

            assertEquals(listOf(PostId to true), fixture.repository.commands)
            assertFalse(viewModel.state.value.autoReadPending)
            release.complete(Unit)
            viewModel.flushEvents()
            viewModel.onEvent(PostDetailScreenEvent.ContentAvailable)
            viewModel.flushEvents()
            assertEquals(listOf(PostId to true), fixture.repository.commands)
        }
    }

    @Test
    fun `Given manual unread When content becomes available Then automatic reading stays suppressed`() = runTest {
        withViewModel { viewModel, fixture ->
            fixture.repository.posts.emit(article())
            viewModel.awaitPost()
            viewModel.onEvent(PostDetailScreenEvent.SetPostRead(postId = PostId, isRead = false))
            viewModel.flushEvents()

            repeat(3) { viewModel.onEvent(PostDetailScreenEvent.ContentAvailable) }
            viewModel.flushEvents()

            assertEquals(listOf(PostId to false), fixture.repository.commands)
            assertFalse(viewModel.state.value.autoReadPending)
        }
    }

    @Test
    fun `Given manual unread When the ViewModel is restored Then content cannot mark it read`() = runTest {
        withViewModel { viewModel, fixture ->
            fixture.repository.posts.emit(article())
            viewModel.awaitPost()
            viewModel.onEvent(PostDetailScreenEvent.SetPostRead(postId = PostId, isRead = false))
            viewModel.flushEvents()
            viewModel.viewModelScope.cancel()
            val restored = fixture.viewModel()
            try {
                backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                    restored.container.refCountStateFlow.collect {}
                }
                restored.awaitPost()
                assertFalse(restored.state.value.autoReadPending)

                restored.onEvent(PostDetailScreenEvent.ContentAvailable)
                restored.flushEvents()

                assertEquals(listOf(PostId to false), fixture.repository.commands)
            } finally {
                restored.viewModelScope.cancel()
            }
        }
    }

    @Test
    fun `Given no post metadata When content availability arrives Then a later post can still be marked read`() = runTest {
        withViewModel { viewModel, fixture ->
            viewModel.onEvent(PostDetailScreenEvent.ContentAvailable)
            viewModel.flushEvents()
            assertTrue(viewModel.state.value.autoReadPending)
            assertEquals(emptyList(), fixture.repository.commands)

            fixture.repository.posts.emit(article())
            viewModel.awaitPost()
            viewModel.onEvent(PostDetailScreenEvent.ContentAvailable)
            viewModel.flushEvents()

            assertEquals(listOf(PostId to true), fixture.repository.commands)
            assertFalse(viewModel.state.value.autoReadPending)
        }
    }

    private suspend fun TestScope.withViewModel(block: suspend TestScope.(PostDetailViewModel, Fixture) -> Unit) {
        val dispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        val savedState = SavedStateHandle()
        startKoin { modules(module { single { savedState } }) }
        val fixture = Fixture(dispatcher)
        val viewModel = fixture.viewModel()
        try {
            backgroundScope.launch(dispatcher) { viewModel.container.refCountStateFlow.collect {} }
            viewModel.flushEvents()
            block(viewModel, fixture)
        } finally {
            viewModel.viewModelScope.cancel()
            fixture.applicationScope.cancel()
            stopKoin()
            Dispatchers.resetMain()
        }
    }

    private suspend fun PostDetailViewModel.awaitPost() = withContext(Dispatchers.Default) {
        withTimeout(5_000) { state.first { it.post != null } }
    }

    private suspend fun PostDetailViewModel.flushEvents() = withContext(Dispatchers.Default) {
        withTimeout(5_000) { intent {}.join() }
    }

    private class Fixture(dispatcher: CoroutineDispatcher) {
        val repository = Repository()
        val contentSettings = ContentSettings()
        private val dispatchers = object : DispatcherProvider {
            override val defaultDispatcher = dispatcher
            override val ioDispatcher = dispatcher
        }
        private val dates = object : DateTimeStringConverter {
            override fun getTimeElapsedOrDateString(instant: Instant): String = "Today"
        }
        private val recentPosts = GetRecentFeedItemsUseCase(repository, dispatchers)
        val applicationScope by lazy { ApplicationScope(dispatchers) }
        private val convert = ConvertToMarkdownUseCase(repository, dispatchers)
        private val save = SavePostContentUseCase(repository, dispatchers)

        fun viewModel() = PostDetailViewModel(
            postId = PostId,
            postInteractor = interactor,
            getPostDetailUseCase = GetPostDetailUseCase(repository, dispatchers),
            getPostContentUseCase = GetPostContentUseCase(
                postRepository = repository,
                savePostContentUseCase = save,
                fetchPostContentUseCase = FetchPostContentUseCase(repository, dispatchers, DPEvent),
                convertPostContentUseCase = convert,
                dispatcherProvider = dispatchers,
                applicationScope = applicationScope,
            ),
            getRssEncodedContentUseCase = GetRssEncodedContentUseCase(
                postRepository = repository,
                convertToMarkdownUseCase = convert,
                savePostContentUseCase = save,
                applicationScope = applicationScope,
                dispatcherProvider = dispatchers,
            ),
            setPostBookmarkedUseCase = SetPostBookmarkedUseCase(repository, dispatchers, DPEvent),
            setContentTextSettingFontScaleUseCase = SetContentTextSettingFontScaleUseCase(contentSettings, dispatchers),
            setContentTextSettingLineHeightScaleUseCase = SetContentTextSettingLineHeightScaleUseCase(contentSettings, dispatchers),
        )

        val interactor = PostInteractor(
            feedInteractor = FeedInteractor(
                extractInitialsUseCase = ExtractInitialsUseCase(),
                getPinnedAndRecentFeedsUseCase = GetPinnedAndRecentFeedsUseCase(UnusedFeeds, dispatchers),
                getRecentFeedItemsUseCase = recentPosts,
                dateTimeStringConverter = dates,
                dispatcherProvider = dispatchers,
            ),
            getRecentFeedItemsUseCase = recentPosts,
            getPaginatedFeedPostsUseCase = GetPaginatedFeedPostsUseCase(repository),
            dispatcherProvider = dispatchers,
            dateTimeStringConverter = dates,
            setPostReadUseCase = SetPostReadUseCase(repository, dispatchers),
            getBrowserOpeningTargetUseCase = GetBrowserOpeningTargetUseCase(contentSettings, dispatchers),
        )
    }

    private class ContentSettings : ContentSettingsRepository {
        var mode = BrowserOpeningMode.IN_APP_BROWSER
        var thrown: Exception? = null

        override fun observeBrowserOpeningMode(defaultMode: BrowserOpeningMode): Flow<BrowserOpeningMode> = flow {
            thrown?.let { throw it }
            emit(mode)
        }

        override suspend fun setBrowserOpeningMode(mode: BrowserOpeningMode) = error("Unused")
        override fun observeContentTextSettings(defaultSettings: ContentTextSettings): Flow<ContentTextSettings> = error("Unused")
        override suspend fun updateContentTextSettings(settings: ContentTextSettings) = error("Unused")
        override suspend fun setContentTextScale(scale: Float) = error("Unused")
        override suspend fun setContentLineHeightScale(spacing: Float) = error("Unused")
    }

    private object UnusedFeeds : FeedRepository {
        override suspend fun upsertFeed(feed: RssFeed) = error("Unused")
        override fun getFeedsListFlow(pagingConfig: PagingConfig): Flow<PagingData<RssFeedWithUserState>> = error("Unused")
        override fun searchFeeds(query: String, snippetLength: Int): Flow<PagingData<RssFeedSearchResult>> = error("Unused")
        override fun getPinnedAndRecentFeeds(maxCount: Int): Flow<List<RssFeedWithUserState>> = error("Unused")
        override fun getFeed(id: UUID): Flow<RssFeedWithUserState> = error("Unused")
        override fun getPinnedFeedFlow(pagingConfig: PagingConfig): Flow<PagingData<RssFeedWithUserState>> = error("Unused")
        override suspend fun extractOpmlFeeds(opml: String): List<OpmlFeedImportData> = error("Unused")
        override suspend fun extractOpmlFeedsFromUrl(url: String): List<OpmlFeedImportData> = error("Unused")
        override suspend fun fetchRssFeed(entry: RssFeedQueueEntry): ParsedFeed = error("Unused")
        override suspend fun deleteFeed(id: UUID) = error("Unused")
        override suspend fun setFeedPinned(id: UUID, pinned: Boolean): Result<Unit> = error("Unused")
        override suspend fun getFeedIdentityBySourceUrl(url: String): RssFeedIdentity? = error("Unused")
    }

    private class Repository : PostRepository {
        var waitFor: CompletableDeferred<Unit>? = null
        var thrown: Exception? = null
        val commands = mutableListOf<Pair<UUID, Boolean>>()
        override suspend fun setPostRead(id: UUID, isRead: Boolean) {
            commands += id to isRead
            thrown?.let { throw it }
            waitFor?.await()
        }
        val posts = MutableSharedFlow<RssPostWithFeedIdentityAndSearch>(replay = 1)
        override fun getPost(id: UUID): Flow<RssPostWithFeedIdentityAndSearch> = posts
        override suspend fun upsertPosts(posts: List<RssFeedPost>) = error("Unused")
        override fun observePosts(query: RssPostQuery, pagingConfig: PagingConfig): Flow<PagingData<RssPostWithFeedIdentityAndSearch>> = emptyFlow()
        override fun observeRecentPosts(maxCount: Int): Flow<List<RssPostWithFeedIdentityAndSearch>> = emptyFlow()
        override suspend fun setPostBookmarked(id: UUID, bookmarked: Boolean) = error("Unused")
        override suspend fun fetchPostContentUseCase(postId: UUID, type: RssFeedPostContentType): RssParsedPostContent = error("Unused")
        override suspend fun getPostContent(postId: UUID, type: RssFeedPostContentType): RssFeedPostContent =
            RssFeedPostContent(postId = postId, type = type, content = "Article body")
        override suspend fun savePostContent(content: RssFeedPostContent) = error("Unused")
        override suspend fun getPostRssEncodedContent(postId: UUID, type: RssFeedPostContentType): RssFeedPostContent =
            RssFeedPostContent(postId = postId, type = type, content = "RSS body")
        override suspend fun getPostDescription(postId: UUID): String? = error("Unused")
        override suspend fun convertToMarkdown(html: String): RssParsedPostContent? = error("Unused")
        override suspend fun getLatestPostPublishedTimeForFeed(feedId: UUID): Long? = error("Unused")
    }

    private fun article(): RssPostWithFeedIdentityAndSearch {
        val feedId = UUID("feed-1")
        return RssPostWithFeedIdentityAndSearch(
            post = RssFeedPost(
                id = PostId,
                feedId = feedId,
                fingerprint = "fingerprint",
                guid = null,
                title = "Article",
                author = null,
                link = ArticleUrl,
                publishedAtMillis = 0,
                description = "Description",
                content = null,
                image = null,
                audio = null,
                video = null,
                sourceName = null,
                sourceUrl = null,
                categories = emptyList(),
                commentsUrl = null,
                youtubeItemData = null,
                rawEnclosure = null,
                createdAtMillis = 0,
                updatedAtMillis = 0,
                pubDate = null,
            ),
            feedIdentity = RssFeedIdentity(
                id = feedId,
                title = "Feed",
                name = null,
                sourceUrl = "https://example.com/rss",
                link = null,
                createdAt = 0,
                updatedAt = 0,
            ),
        )
    }

    private companion object {
        val PostId = UUID("post-1")
        const val ArticleUrl = "https://example.com/article?source=feed#section"
    }
}
