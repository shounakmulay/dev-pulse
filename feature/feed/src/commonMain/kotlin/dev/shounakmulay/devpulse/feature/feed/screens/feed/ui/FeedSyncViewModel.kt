package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shounakmulay.devpulse.core.domain.feed.queue.InitialiseFeedQueueProcessingUseCase
import dev.shounakmulay.devpulse.core.logging.DPLogger
import dev.shounakmulay.devpulse.core.sync.BackgroundSyncScheduler
import dev.shounakmulay.devpulse.core.sync.DevPulsePeriodicWorkerExitingWorkPolicy
import dev.shounakmulay.devpulse.core.sync.DevPulseWorkRequest
import dev.shounakmulay.devpulse.core.sync.DevPulseWorkRequestConstraints
import dev.shounakmulay.devpulse.core.sync.DevPulseWorkerType
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import kotlin.time.Duration.Companion.hours

@KoinViewModel
class FeedSyncViewModel(
    private val initialiseFeedQueueProcessingUseCase: InitialiseFeedQueueProcessingUseCase,
    private val backgroundSyncScheduler: BackgroundSyncScheduler,
    logger: DPLogger
) : ViewModel() {
    private val logger = logger.withTag(Tag)

    init {
        logger.d { "FeedQueueViewModel created" }
    }

    fun init() {
        logger.d { "Feed queue initialisation requested" }
        initialiseFeedQueueProcessingUseCase()
        scheduleFeedSync()
    }

    private fun scheduleFeedSync() {
        viewModelScope.launch {
            backgroundSyncScheduler.initialise(
                listOf(
                    DevPulseWorkRequest.PeriodicWorkRequest(
                        identifier = DevPulseWorkerType.FEED_SYNC,
                        interval = 1.hours,
                        existingPeriodicWorkPolicy = DevPulsePeriodicWorkerExitingWorkPolicy.KEEP,
                        constraints = DevPulseWorkRequestConstraints.requiresNetwork(),
                    )
                )
            )
        }
    }

    override fun onCleared() {
        logger.d { "FeedQueueViewModel cleared" }
        super.onCleared()
    }

    private companion object {
        const val Tag = "FeedQueueViewModel"
    }
}
