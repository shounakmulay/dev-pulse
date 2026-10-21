package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.ui.model

import kotlinx.serialization.Serializable

@Serializable
enum class ReaderVisitReadDecision { Pending, Consumed, SuppressedByUser }

@Serializable
data class ReaderVisitReadState(
    val decision: ReaderVisitReadDecision = ReaderVisitReadDecision.Pending,
) {
    fun consume(): ReaderVisitReadState = copy(decision = ReaderVisitReadDecision.Consumed)

    fun suppress(): ReaderVisitReadState = copy(decision = ReaderVisitReadDecision.SuppressedByUser)
}
