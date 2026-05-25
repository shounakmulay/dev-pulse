package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui

import androidx.lifecycle.viewModelScope
import dev.shounakmulay.devpulse.core.domain.feed.ImportFeedUseCase
import dev.shounakmulay.devpulse.core.domain.feed.ObserveFeedQueueForUrlsUseCase
import dev.shounakmulay.devpulse.core.domain.feed.UrlValidationError
import dev.shounakmulay.devpulse.core.domain.feed.UrlValidationResult
import dev.shounakmulay.devpulse.core.domain.feed.ValidateUrlUseCase
import dev.shounakmulay.devpulse.core.domain.models.feed.AddFeedData
import dev.shounakmulay.devpulse.core.ui.effect.Effect
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.model.UIAddFeedData
import dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.model.UIFeedQueueData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@OptIn(ExperimentalCoroutinesApi::class)
@KoinViewModel
class AddFeedViewModel(
    private val importFeedUseCase: ImportFeedUseCase,
    private val observeFeedQueueForUrlsUseCase: ObserveFeedQueueForUrlsUseCase,
    private val validateUrlUseCase: ValidateUrlUseCase
) : MviViewModel<AddFeedScreenState, Effect>(), EventHandler<AddFeedScreenEvent> {
    override fun createInitialState() = AddFeedScreenState()
    override fun createStateSerializer() = AddFeedScreenState.serializer()

    init {
        state
            .map { it.processingData.map { queueData -> queueData.addFeedData.url } }
            .distinctUntilChanged()
            .flatMapLatest {
                observeFeedQueueForUrlsUseCase(it)
            }
            .map {
                setState {
                    copy(
                        processingData = processingData.map { feedQueueData ->
                            val processingEntry = it
                                .firstOrNull { queueEntry -> queueEntry.url == feedQueueData.addFeedData.url }

                            if (processingEntry == null) feedQueueData
                            else {
                                feedQueueData.copy(
                                    status = processingEntry.status
                                )
                            }
                        }
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onEvent(event: AddFeedScreenEvent) {
        when (event) {
            is AddFeedScreenEvent.ImportFeeds -> onImportFeeds()
            is AddFeedScreenEvent.UpdateUrl -> onUpdateUrl(event.id, event.url)
            is AddFeedScreenEvent.UpdateName -> onUpdateName(event.id, event.name)
            is AddFeedScreenEvent.Delete -> onDelete(event.id)
            AddFeedScreenEvent.Add -> onAdd()
            is AddFeedScreenEvent.ToggleExpanded -> onToggleExpanded(event.id)
            is AddFeedScreenEvent.ValidateSourceUrl -> onValidateSourceUrl(event.id)
            AddFeedScreenEvent.ClearAllStatus -> onClearAllStatus()
            AddFeedScreenEvent.GoToQueue -> onGoToQueue()
            AddFeedScreenEvent.RetryFailedImports -> onRetryFailedImports()
            AddFeedScreenEvent.ToggleCollapseAll -> onToggleCollapseAll()
        }
    }

    private fun onClearAllStatus() {
        setState {
            copy(processingData = emptyList())
        }
    }

    private fun onGoToQueue() {}

    private fun onRetryFailedImports() {}

    private fun onToggleCollapseAll() {
        val expanded = state.value.addFeedDataList.all { it.expanded }
        setState {
            copy(
                addFeedDataList = addFeedDataList.map { it.copy(expanded = !expanded) }
            )
        }
    }

    private fun onValidateSourceUrl(id: String) {
        val addFeedData = state.value.addFeedDataList.firstOrNull { it.id == id } ?: return
        when (val sourceUrlValidationResult = validateUrlUseCase(addFeedData.url)) {
            is UrlValidationResult.Invalid -> setState {
                copy(
                    addFeedDataList = addFeedDataList.map {
                        if (it.id == id) {
                            it.copy(
                                error = when (sourceUrlValidationResult.error) {
                                    UrlValidationError.EMPTY,
                                    UrlValidationError.UNSUPPORTED_SCHEME,
                                    UrlValidationError.MISSING_SCHEME_DELIMITER,
                                    UrlValidationError.INVALID_CHARACTERS,
                                    UrlValidationError.MISSING_HOST,
                                    UrlValidationError.INVALID_HOST -> UIAddFeedData.ValidationError.INVALID_SOURCE_URL
                                }
                            )
                        } else it
                    }
                )
            }

            is UrlValidationResult.Valid -> setState {
                copy(
                    addFeedDataList = addFeedDataList.map {
                        if (it.id == id) {
                            it.copy(url = sourceUrlValidationResult.url, error = null)
                        } else it
                    }
                )
            }
        }
    }

    private fun onToggleExpanded(id: String) {
        setState {
            copy(
                addFeedDataList = addFeedDataList.map {
                    if (it.id == id) {
                        it.copy(expanded = !it.expanded)
                    } else it
                }
            )
        }
    }

    private fun onAdd() {
        setState {
            copy(
                addFeedDataList = addFeedDataList + UIAddFeedData.empty()
            )
        }
    }

    private fun onDelete(id: String) {
        setState {
            var newList = addFeedDataList.mapNotNull {
                if (it.id == id) null else it
            }

            if (newList.size == 1) {
                newList = newList.map {
                    it.copy(expanded = true)
                }
            }

            copy(
                addFeedDataList = newList
            )
        }
    }

    private fun onUpdateName(id: String, name: String) {
        setState {
            copy(
                addFeedDataList = addFeedDataList.map {
                    if (it.id == id) it.copy(name = name) else it
                }
            )
        }
    }

    private fun onUpdateUrl(id: String, url: String) {
        setState {
            copy(
                addFeedDataList = addFeedDataList.map {
                    if (it.id == id) {
                        it.copy(
                            url = url
                        )
                    } else it
                }
            )
        }
    }

    fun onImportFeeds() = viewModelScope.launch {
        val validatedData = state.value.addFeedDataList
            .map {
                async { it to validateUrlUseCase(it.url) }
            }
            .awaitAll()
            .map { (data, validationResult) ->
                when (validationResult) {
                    is UrlValidationResult.Invalid -> data.copy(
                        error = UIAddFeedData.ValidationError.INVALID_SOURCE_URL,
                    )

                    is UrlValidationResult.Valid -> data.copy(
                        url = validationResult.url,
                        error = null
                    )
                }
            }
            .groupBy { it.error == null }

        val validData = validatedData[true].orEmpty()
        var invalidData = validatedData[false].orEmpty()

        if (invalidData.size == 1) invalidData = invalidData.map { it.copy(expanded = true) }

        setState {
            copy(
                addFeedDataList = invalidData,
                processingData = validData.map {
                    UIFeedQueueData(
                        addFeedData = it,
                        status = null
                    )
                }
            )
        }

        intent {
            importFeedUseCase(validData.map {
                AddFeedData(
                    url = it.url,
                    name = it.name,
                    type = it.type,
                    tags = it.tags,
                    folders = it.folders,
                )
            })
        }
    }
}
