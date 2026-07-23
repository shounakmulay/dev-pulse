package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui

import androidx.lifecycle.viewModelScope
import dev.shounakmulay.devpulse.core.domain.feed.NormalizeUrlUseCase
import dev.shounakmulay.devpulse.core.domain.feed.ValidateUrlUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.ExtractOpmlFeedsUseCase
import dev.shounakmulay.devpulse.core.domain.feed.feed.ImportFeedUseCase
import dev.shounakmulay.devpulse.core.domain.feed.queue.ObserveFeedQueueForUrlsUseCase
import dev.shounakmulay.devpulse.core.domain.models.feed.AddFeedData
import dev.shounakmulay.devpulse.core.domain.models.feed.OpmlFeedImportData
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueStatus
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.model.UIAddFeedData
import dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.model.UIFeedQueueData
import devpulse.core.resources.generated.resources.add_feed_opml_empty_error
import devpulse.core.resources.generated.resources.add_feed_opml_no_feeds_error
import devpulse.core.resources.generated.resources.add_feed_opml_process_error
import devpulse.core.resources.generated.resources.enter_feed_url
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
    private val extractOpmlFeedsUseCase: ExtractOpmlFeedsUseCase,
    private val observeFeedQueueForUrlsUseCase: ObserveFeedQueueForUrlsUseCase,
    private val normalizeUrlUseCase: NormalizeUrlUseCase,
    private val validateUrlUseCase: ValidateUrlUseCase
) : MviViewModel<AddFeedScreenState, AddFeedScreenEffect>(AddFeedScreenState()), EventHandler<AddFeedScreenEvent> {
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
            is AddFeedScreenEvent.MoveFailedImportToEdit -> onMoveFailedImportToEdit(event.id)
            AddFeedScreenEvent.ToggleCollapseAll -> onToggleCollapseAll()
            is AddFeedScreenEvent.ExtractOpmlFeedUrl -> onExtractOpmlFeeds()
            AddFeedScreenEvent.OpenImportOpmlBottomSheet -> onOpenImportOpmlBottomSheet()
            is AddFeedScreenEvent.UpdateOpmlUrl -> onUpdateOpmlUrl(event.url)
        }
    }

    private fun onOpenImportOpmlBottomSheet() {
        postEffect(AddFeedScreenEffect.SetAddOpmlBottomSheetVisible(true))
    }

    private fun onExtractOpmlFeeds() = intent {
        setState { copy(isOpmlLoading = true) }
        val normalizedUrl = normalizeUrlUseCase(state.opmlUrl)
        if (!validateUrlUseCase(normalizedUrl)) {
            setState {
                copy(
                    isOpmlLoading = false,
                    opmlImportError = TextResource.fromStringRes(stringRes.add_feed_opml_empty_error)
                )
            }
            return@intent
        }

        val opmlExtractionResult = extractOpmlFeedsUseCase.fromUrl(normalizedUrl)

        opmlExtractionResult.fold(
            onSuccess = ::onOpmlExtractionSuccess,
            onFailure = {
                setState {
                    copy(
                        isOpmlLoading = false,
                        opmlImportError = TextResource.fromStringRes(stringRes.add_feed_opml_process_error)
                    )
                }
            }
        )

    }

    private fun onOpmlExtractionSuccess(
        feeds: List<OpmlFeedImportData>
    ) {
        if (feeds.isEmpty()) {
            setState {
                copy(
                    isOpmlLoading = false,
                    opmlImportError = TextResource.fromStringRes(stringRes.add_feed_opml_no_feeds_error)
                )
            }
            return
        }

        setState {
            val importedData = feeds.map(UIAddFeedData::fromOpml)
            val currentData =
                addFeedDataList.takeUnless { it.isSingleEmptyEntry() }.orEmpty()
            copy(
                addFeedDataList = currentData + importedData,
                isOpmlLoading = false,
                opmlUrl = "",
                opmlImportError = null
            )
        }

        postEffect(AddFeedScreenEffect.SetAddOpmlBottomSheetVisible(false))
    }

    private fun onUpdateOpmlUrl(url: String) {
        setState {
            copy(
                opmlUrl = url,
                opmlImportError = null
            )
        }
    }

    private fun onClearAllStatus() {
        setState {
            copy(processingData = emptyList())
        }
    }

    private fun onGoToQueue() {}

    private fun onRetryFailedImports() = viewModelScope.launch {
        val failedData = state.value.processingData
            .filter { it.status == RssFeedQueueStatus.FAILED }
            .map { it.addFeedData.toAddFeedData() }

        if (failedData.isEmpty()) return@launch

        intent {
            importFeedUseCase(failedData)
        }
    }

    private fun onMoveFailedImportToEdit(id: String) {
        setState {
            val failedData = processingData
                .firstOrNull {
                    it.addFeedData.id == id && it.status == RssFeedQueueStatus.FAILED
                }
                ?.addFeedData
                ?.copy(
                    expanded = true,
                    error = null
                ) ?: return@setState this

            copy(
                processingData = processingData.filterNot {
                    it.addFeedData.id == id && it.status == RssFeedQueueStatus.FAILED
                },
                addFeedDataList = addFeedDataList + failedData
            )
        }
    }

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
        val normalizedUrl = normalizeUrlUseCase(addFeedData.url)
        setState {
            copy(
                addFeedDataList = addFeedDataList.map {
                    if (it.id != id) return@map it

                    it.copy(url = normalizedUrl, error = null).withCollapsedHeaderText()
                }
            )
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
                    if (it.id == id) it.copy(name = name).withCollapsedHeaderText() else it
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
                        ).withCollapsedHeaderText()
                    } else it
                }
            )
        }
    }

    fun onImportFeeds() = viewModelScope.launch {
        val validatedData = state.value.addFeedDataList
            .map {
                async { it to normalizeUrlUseCase(it.url) }
            }
            .awaitAll()
            .map { (data, normalizedUrl) ->
                data.copy(
                    url = normalizedUrl,
                    error = null
                ).withCollapsedHeaderText()
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
            importFeedUseCase(validData.map { it.toAddFeedData() })
        }
    }

    private fun UIAddFeedData.toAddFeedData(): AddFeedData {
        return AddFeedData(
            url = url,
            name = name,
            type = type,
            tags = tags,
            folders = folders,
        )
    }

    private fun UIAddFeedData.withCollapsedHeaderText(): UIAddFeedData {
        val text = name?.ifBlank { null }
            ?: url.ifBlank { null }
        return copy(
            collapsedHeaderText = text?.let(TextResource::fromText)
                ?: TextResource.fromStringRes(stringRes.enter_feed_url)
        )
    }

    private fun List<UIAddFeedData>.isSingleEmptyEntry(): Boolean {
        val onlyEntry = singleOrNull() ?: return false
        return onlyEntry.url.isBlank() && onlyEntry.name.isNullOrBlank()
    }
}
