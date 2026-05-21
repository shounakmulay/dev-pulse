package dev.shounakmulay.devpulse.core.data.feed.repository

import app.cash.turbine.test
import dev.shounakmulay.devpulse.core.common.time.DateTimeProvider
import dev.shounakmulay.devpulse.core.data.db.dao.FeedProcessingJobDao
import dev.shounakmulay.devpulse.core.data.db.model.feed.LocalFeedProcessingJob
import dev.shounakmulay.devpulse.core.domain.models.feed.processing.FeedProcessingExecutionState
import dev.shounakmulay.devpulse.core.domain.models.feed.processing.FeedProcessingJobType
import dev.shounakmulay.devpulse.core.domain.models.feed.processing.FeedProcessingStage
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FeedProcessingJobRepositoryImplTest {

    @Test
    fun `Given source URL When enqueue import Then queued job is stored and job ID is returned`() = runTest {
        val context = createRepositoryContext()

        val jobId = context.repository.enqueueFeedImport("https://example.com/rss.xml")

        context.repository.observeJob(jobId).test {
            val job = awaitItem()
            assertEquals("job-1", jobId)
            assertEquals(FeedProcessingJobType.Import, job?.type)
            assertEquals(FeedProcessingStage.Queued, job?.stage)
            assertEquals(FeedProcessingExecutionState.Waiting, job?.executionState)
            assertEquals("https://example.com/rss.xml", job?.sourceUrl)
            assertNull(job?.feedId)
            assertEquals(0, job?.processedCount)
            assertNull(job?.totalCount)
            assertEquals(0, job?.failedCount)
            assertEquals(1000L, job?.queuedAt)
            assertNull(job?.startedAt)
            assertNull(job?.completedAt)
            assertEquals(1000L, job?.updatedAt)
            assertNull(job?.errorDetail)
            assertTrue(job?.isActive == true)
        }
        verifySuspend {
            context.dao.enqueueImportJob(any())
        }
    }

    @Test
    fun `Given active import for exact raw URL When enqueue same URL Then active job ID is reused`() = runTest {
        val context = createRepositoryContext()

        val firstJobId = context.repository.enqueueFeedImport(" https://example.com/rss.xml ")
        val secondJobId = context.repository.enqueueFeedImport(" https://example.com/rss.xml ")

        assertEquals(firstJobId, secondJobId)
        assertEquals(1, context.insertedJobs.size)
    }

    @Test
    fun `Given active import for raw URL When enqueue different raw URL Then separate job is created`() = runTest {
        val context = createRepositoryContext()

        val firstJobId = context.repository.enqueueFeedImport("https://example.com/rss.xml")
        val secondJobId = context.repository.enqueueFeedImport(" https://example.com/rss.xml ")

        assertEquals("job-1", firstJobId)
        assertEquals("job-2", secondJobId)
        assertEquals(2, context.insertedJobs.size)
    }

    @Test
    fun `Given observed job When stored job changes Then observer receives mapped state`() = runTest {
        val context = createRepositoryContext()
        val jobId = context.repository.enqueueFeedImport("https://example.com/rss.xml")

        context.repository.observeJob(jobId).test {
            assertEquals(FeedProcessingStage.Queued, awaitItem()?.stage)

            context.replaceJob(
                context.insertedJobs.single().copy(
                    stage = FeedProcessingStage.Failed,
                    executionState = FeedProcessingExecutionState.Failed,
                    completedAt = 2000L,
                    updatedAt = 2000L,
                    errorDetail = "network failed"
                )
            )

            val job = awaitItem()
            assertEquals(FeedProcessingStage.Failed, job?.stage)
            assertEquals(FeedProcessingExecutionState.Failed, job?.executionState)
            assertEquals("network failed", job?.errorDetail)
            assertFalse(job?.isActive == true)
        }
    }

    private fun createRepositoryContext(
        ids: List<String> = listOf("job-1", "job-2", "job-3")
    ): RepositoryTestContext {
        val dao = mock<FeedProcessingJobDao>()
        val idGenerator = mock<FeedProcessingJobIdGenerator>()
        val dateTimeProvider = mock<DateTimeProvider>()
        val insertedJobs = mutableListOf<LocalFeedProcessingJob>()
        val jobFlows = mutableMapOf<String, MutableStateFlow<LocalFeedProcessingJob?>>()
        val idIterator = ids.iterator()

        every { idGenerator.nextId() } calls { idIterator.next() }
        every { dateTimeProvider.nowEpochMilliseconds() } returns 1000L
        every { dao.observeJob(any()) } calls { (id: String) ->
            jobFlows.getOrPut(id) {
                MutableStateFlow(insertedJobs.firstOrNull { it.id == id })
            }
        }
        everySuspend { dao.enqueueImportJob(any()) } calls { (candidate: LocalFeedProcessingJob) ->
            val activeJob = insertedJobs.firstOrNull { job ->
                job.type == FeedProcessingJobType.Import &&
                    job.sourceUrl == candidate.sourceUrl &&
                    job.executionState in setOf(
                        FeedProcessingExecutionState.Waiting,
                        FeedProcessingExecutionState.Running
                    )
            }
            if (activeJob != null) {
                activeJob
            } else {
                insertedJobs += candidate
                jobFlows.getOrPut(candidate.id) { MutableStateFlow(null) }.value = candidate
                candidate
            }
        }

        return RepositoryTestContext(
            repository = FeedProcessingJobRepositoryImpl(
                jobDao = dao,
                idGenerator = idGenerator,
                dateTimeProvider = dateTimeProvider
            ),
            jobDao = dao,
            insertedJobs = insertedJobs,
            jobFlows = jobFlows
        )
    }

    private class RepositoryTestContext(
        val repository: FeedProcessingJobRepositoryImpl,
        val jobDao: FeedProcessingJobDao,
        val insertedJobs: MutableList<LocalFeedProcessingJob>,
        private val jobFlows: MutableMap<String, MutableStateFlow<LocalFeedProcessingJob?>>
    ) {
        val dao: FeedProcessingJobDao get() = jobDao

        fun replaceJob(job: LocalFeedProcessingJob) {
            val index = insertedJobs.indexOfFirst { it.id == job.id }
            insertedJobs[index] = job
            jobFlows.getOrPut(job.id) { MutableStateFlow(null) }.value = job
        }
    }
}
