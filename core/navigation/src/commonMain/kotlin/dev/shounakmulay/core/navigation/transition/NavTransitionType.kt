package dev.shounakmulay.core.navigation.transition

internal sealed interface NavTransitionType {
    data class TabSwitch(val direction: TabSwitchDirection) : NavTransitionType
    data object PushInStack : NavTransitionType
    data class PopInStack(val predictive: Boolean) : NavTransitionType
}