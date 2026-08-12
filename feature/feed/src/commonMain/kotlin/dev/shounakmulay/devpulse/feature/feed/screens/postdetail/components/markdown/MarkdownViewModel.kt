package dev.shounakmulay.devpulse.feature.feed.screens.postdetail.components.markdown

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikepenz.markdown.model.State
import com.mikepenz.markdown.model.parseMarkdownFlow
import dev.shounakmulay.devpulse.core.logging.logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class MarkdownViewModel : ViewModel() {
    private val logger = logger<MarkdownViewModel>()
    var markdownContent = MutableStateFlow("")
        private set

    private val cache = hashMapOf<String, State.Success>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val markdownState = markdownContent
        .filter { it.isNotBlank() }
        .flatMapLatest { content ->
            if (cache.contains(content)) {
                logger.d { "Returning cached value for ${content.take(10)}" }
                flowOf(cache.getValue(content))
            } else {
                logger.d { "Parsing markdown for ${content.take(10)}" }
                parseMarkdownFlow(content).onEach {
                    if (it is State.Success) {
                        logger.d { "Caching value for ${content.take(10)}" }
                        cache[content] = it
                    }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = State.Loading()
        )

    fun setMarkdownContent(content: String) {
        markdownContent.value = content
    }
}