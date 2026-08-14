package dev.shounakmulay.devpulse.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import dev.shounakmulay.devpulse.core.logging.DPLogger
import dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.FeedQueueViewModel
import org.koin.compose.koinInject

@Composable
fun AppLevelInitialisers() {
    val feedQueueViewModel: FeedQueueViewModel = koinInject()
    val logger = koinInject<DPLogger>().withTag("DevPulseApp")

    LaunchedEffect(Unit) {
        logger.i { "Starting app-level feed queue initialiser" }
        feedQueueViewModel.init()
    }
}