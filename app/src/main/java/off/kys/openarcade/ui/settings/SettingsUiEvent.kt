package off.kys.openarcade.ui.settings

import off.kys.openarcade.domain.model.LauncherSection

sealed interface SettingsUiEvent {
    data class ToggleImmersiveMode(val enabled: Boolean) : SettingsUiEvent
    data class ToggleKeepScreenOn(val enabled: Boolean) : SettingsUiEvent
    data class ToggleShowScrollbar(val enabled: Boolean) : SettingsUiEvent
    data class ToggleReduceAnimations(val enabled: Boolean) : SettingsUiEvent
    data class ToggleHapticFeedback(val enabled: Boolean) : SettingsUiEvent
    data class ToggleLaunchAnimation(val enabled: Boolean) : SettingsUiEvent
    data class SetScreenOrientation(val orientation: ScreenOrientation) : SettingsUiEvent
    data class SetGridColumns(val columns: GridColumns) : SettingsUiEvent
    data class ToggleSection(val section: LauncherSection, val visible: Boolean) : SettingsUiEvent
    data object ResetAll : SettingsUiEvent
}
