package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui

import androidx.lifecycle.ViewModel
import dev.shounakmulay.devpulse.core.domain.feed.InitialiseFeedQueueProcessingUseCase
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class FeedQueueViewModel(
    private val initialiseFeedQueueProcessingUseCase: InitialiseFeedQueueProcessingUseCase
) : ViewModel() {

    fun init() {
        initialiseFeedQueueProcessingUseCase()
    }
}