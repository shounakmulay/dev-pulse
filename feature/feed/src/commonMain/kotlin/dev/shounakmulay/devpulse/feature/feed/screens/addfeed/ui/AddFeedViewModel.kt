package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui

import androidx.lifecycle.viewModelScope
import dev.shounakmulay.devpulse.core.domain.feed.ImportFeedUseCase
import dev.shounakmulay.devpulse.core.domain.feed.UrlValidationError
import dev.shounakmulay.devpulse.core.domain.feed.UrlValidationResult
import dev.shounakmulay.devpulse.core.domain.feed.ValidateUrlUseCase
import dev.shounakmulay.devpulse.core.domain.models.feed.AddFeedData
import dev.shounakmulay.devpulse.core.ui.effect.Effect
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.model.UIAddFeedData
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class AddFeedViewModel(
    private val importFeedUseCase: ImportFeedUseCase,
    private val validateUrlUseCase: ValidateUrlUseCase
) : MviViewModel<AddFeedScreenState, Effect>(), EventHandler<AddFeedScreenEvent> {
    override fun createInitialState() = AddFeedScreenState()
    override fun createStateSerializer() = AddFeedScreenState.serializer()

    override fun onEvent(event: AddFeedScreenEvent) {
        when (event) {
            is AddFeedScreenEvent.ImportFeeds -> onImportFeeds()
            is AddFeedScreenEvent.UpdateUrl -> onUpdateUrl(event.id, event.url)
            is AddFeedScreenEvent.UpdateName -> onUpdateName(event.id, event.name)
            is AddFeedScreenEvent.Delete -> onDelete(event.id)
            AddFeedScreenEvent.Add -> onAdd()
            is AddFeedScreenEvent.ToggleExpanded -> onToggleExpanded(event.id)
            is AddFeedScreenEvent.ValidateSourceUrl -> onValidateSourceUrl(event.id)
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
        state.value.addFeedDataList.map {
            async { it to validateUrlUseCase(it.url) }
        }.awaitAll()
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

        intent {
            importFeedUseCase.invoke(state.addFeedDataList.map {
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
