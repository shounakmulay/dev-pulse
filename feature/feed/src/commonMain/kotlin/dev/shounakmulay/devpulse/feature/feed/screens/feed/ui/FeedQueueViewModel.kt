package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui

import androidx.lifecycle.ViewModel
import dev.shounakmulay.devpulse.core.domain.feed.InitialiseFeedQueueProcessingUseCase
import dev.shounakmulay.devpulse.core.logging.DPLogger
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class FeedQueueViewModel(
    private val initialiseFeedQueueProcessingUseCase: InitialiseFeedQueueProcessingUseCase,
    logger: DPLogger
) : ViewModel() {
    private val logger = logger.withTag(Tag)

    init {
        logger.d { "FeedQueueViewModel created" }
    }

    fun init() {
        logger.d { "Feed queue initialisation requested" }
        initialiseFeedQueueProcessingUseCase()
    }

    override fun onCleared() {
        logger.d { "FeedQueueViewModel cleared" }
        super.onCleared()
    }

    private companion object {
        const val Tag = "FeedQueueViewModel"
    }
}
