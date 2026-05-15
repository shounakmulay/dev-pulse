package dev.shounakmulay.devpulse.feature.settings.screens.settings.ui
import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import dev.shounakmulay.devpulse.core.common.extensions.onEachSuccess
import dev.shounakmulay.devpulse.core.domain.models.ThemeSettings
import dev.shounakmulay.devpulse.core.domain.settings.ObserveThemeSettingsUseCase
import dev.shounakmulay.devpulse.core.domain.settings.SetThemeSettingsUseCase
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.text.TextResource
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import dev.shounakmulay.devpulse.feature.settings.screens.settings.data.SettingsListItem
import dev.shounakmulay.devpulse.feature.settings.screens.settings.data.SettingsSingleChoiceKey
import dev.shounakmulay.devpulse.feature.settings.screens.settings.data.SettingsToggleKey
import dev.shounakmulay.devpulse.feature.settings.screens.settings.data.ThemeSingleChoiceOptions
import devpulse.core.resources.generated.resources.black_mode
import devpulse.core.resources.generated.resources.design_system_board
import devpulse.core.resources.generated.resources.developer_tools
import devpulse.core.resources.generated.resources.select_app_theme
import devpulse.core.resources.generated.resources.theme
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class SettingsViewModel(
    private val observeThemeSettingsUseCase: ObserveThemeSettingsUseCase,
    private val setThemeSettingsUseCase: SetThemeSettingsUseCase
) : MviViewModel<SettingsScreenState, SettingsScreenEffect>(),
    EventHandler<SettingsScreenEvent> {

    init {
        observeThemeSettingsUseCase()
            .onEachSuccess { themeSettings ->
                if (themeSettings != null) {
                    setState {
                        copy(
                            themeMode = themeSettings.mode,
                            isBlackMode = themeSettings.blackMode
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    override fun createInitialState() = SettingsScreenState(
        settingsList = persistentListOf(
            SettingsListItem.SectionHeading(TextResource.fromStringRes(stringRes.theme)),
            SettingsListItem.SingleChoice(
                title = TextResource.fromStringRes(stringRes.select_app_theme),
                key = SettingsSingleChoiceKey.THEME,
            ),
            SettingsListItem.Toggle(
                title = TextResource.fromStringRes(stringRes.black_mode),
                key = SettingsToggleKey.BLACK_MODE
            ),
            SettingsListItem.SectionHeading(TextResource.fromStringRes(stringRes.developer_tools)),
            SettingsListItem.SubPageLink(
                heading = TextResource.fromStringRes(stringRes.design_system_board),
                supportingText = null,
                linkToScreen = Screen.DeveloperTools.DesignSystemBoard
            )
        )
    )

    override fun createStateSerializer() = SettingsScreenState.serializer()

    override fun onEvent(event: SettingsScreenEvent) {
        when (event) {
            is SettingsScreenEvent.OnSubPageLinkClick -> onNavigateToSubPage(event.linkToScreen)
            is SettingsScreenEvent.OnListItemToggled -> onItemToggled(event.item, event.value)
            is SettingsScreenEvent.OnSingleChoiceUpdated -> onSingleChoiceUpdated(event.item, event.value)
        }
    }

    private fun onSingleChoiceUpdated(
        item: SettingsListItem.SingleChoice,
        value: SettingsListItem.SingleChoice.SingleChoiceOptions
    ) {
        when (item.key) {
            SettingsSingleChoiceKey.THEME -> {
                val theme = value as? ThemeSingleChoiceOptions ?: return
                updateThemeMode(theme)
            }
        }
    }

    private fun onItemToggled(
        item: SettingsListItem.Toggle,
        value: Boolean
    ) {
        when (item.key) {
            SettingsToggleKey.BLACK_MODE -> toggleBlackMode(value)
        }
    }

    private fun toggleBlackMode(value: Boolean) {
        val currentState = state.value
        if (!currentState.canToggleBlackMode) return
        setState { copy(isBlackMode = value) }
        viewModelScope.launch {
            setThemeSettingsUseCase(
                ThemeSettings(
                    mode = currentState.themeMode,
                    blackMode = value
                )
            ).onFailure {
                setState {
                    copy(
                        themeMode = currentState.themeMode,
                        isBlackMode = currentState.isBlackMode
                    )
                }
            }
        }
    }

    private fun updateThemeMode(value: ThemeSingleChoiceOptions) {
        val currentState = state.value
        val themeMode = value.toThemeMode()
        setState { copy(themeMode = themeMode) }
        viewModelScope.launch {
            setThemeSettingsUseCase(
                ThemeSettings(
                    mode = themeMode,
                    blackMode = currentState.isBlackMode
                )
            ).onFailure {
                setState {
                    copy(
                        themeMode = currentState.themeMode,
                        isBlackMode = currentState.isBlackMode
                    )
                }
            }
        }
    }

    private fun onNavigateToSubPage(linkToScreen: Screen) {
        when (linkToScreen) {
            Screen.DeveloperTools.DesignSystemBoard -> navigateToDesignSystemBoard()
            else -> {}
        }
    }

    private fun navigateToDesignSystemBoard() =
        postEffect(SettingsScreenEffect.NavigateToDesignSystemBoard)
}
