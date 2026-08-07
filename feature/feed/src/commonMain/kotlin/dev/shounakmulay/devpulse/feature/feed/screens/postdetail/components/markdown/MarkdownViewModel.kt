package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.components.markdown

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikepenz.markdown.model.State
import com.mikepenz.markdown.model.parseMarkdownFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class MarkdownViewModel(content: String) : ViewModel() {

    val markdownState = parseMarkdownFlow(content)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = State.Loading()
        )
}