package off.kys.openarcade.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import off.kys.openarcade.data.local.ArcadePreferences
import off.kys.openarcade.domain.model.LauncherSection

class SettingsViewModel(
    private val prefs: ArcadePreferences
) : ViewModel() {

    val state: StateFlow<SettingsState> = combine(
        combine(
            prefs.immersiveMode,
            prefs.keepScreenOn,
            prefs.showScrollbar,
            prefs.reduceAnimations,
            prefs.hapticFeedback
        ) { immersive, keepScreen, scrollbar, reduceAnim, haptic ->
            FiveTuple(immersive, keepScreen, scrollbar, reduceAnim, haptic)
        },
        combine(
            prefs.launchAnimation,
            prefs.screenOrientation,
            prefs.gridColumns,
            prefs.visibleSections
        ) { launchAnim, orientation, columns, sections ->
            FourTuple(launchAnim, orientation, columns, sections)
        }
    ) { firstGroup, secondGroup ->
        SettingsState(
            immersiveMode = firstGroup.immersive,
            keepScreenOn = firstGroup.keepScreen,
            showScrollbar = firstGroup.scrollbar,
            reduceAnimations = firstGroup.reduceAnim,
            hapticFeedback = firstGroup.haptic,
            launchAnimation = secondGroup.launchAnim,
            screenOrientation = secondGroup.orientation,
            gridColumns = secondGroup.columns,
            showFavoritesSection = secondGroup.sections.contains(LauncherSection.FAVORITES),
            showAnalyticsSection = secondGroup.sections.contains(LauncherSection.ANALYTICS),
            showRecentSection = secondGroup.sections.contains(LauncherSection.RECENT_ACTIVITY),
            showSystemStatus = secondGroup.sections.contains(LauncherSection.SYSTEM_STATUS)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsState()
    )

    fun onEvent(event: SettingsUiEvent) = when (event) {
        is SettingsUiEvent.ToggleImmersiveMode -> prefs.setImmersiveMode(event.enabled)
        is SettingsUiEvent.ToggleKeepScreenOn -> prefs.setKeepScreenOn(event.enabled)
        is SettingsUiEvent.ToggleShowScrollbar -> prefs.setShowScrollbar(event.enabled)
        is SettingsUiEvent.ToggleReduceAnimations -> prefs.setReduceAnimations(event.enabled)
        is SettingsUiEvent.ToggleHapticFeedback -> prefs.setHapticFeedback(event.enabled)
        is SettingsUiEvent.ToggleLaunchAnimation -> prefs.setLaunchAnimation(event.enabled)
        is SettingsUiEvent.SetScreenOrientation -> prefs.setScreenOrientation(event.orientation)
        is SettingsUiEvent.SetGridColumns -> prefs.setGridColumns(event.columns)
        is SettingsUiEvent.ToggleSection -> prefs.setSectionVisible(event.section, event.visible)
        is SettingsUiEvent.ResetAll -> prefs.resetAll()
    }

    private data class FiveTuple(
        val immersive: Boolean,
        val keepScreen: Boolean,
        val scrollbar: Boolean,
        val reduceAnim: Boolean,
        val haptic: Boolean
    )

    private data class FourTuple(
        val launchAnim: Boolean,
        val orientation: ScreenOrientation,
        val columns: GridColumns,
        val sections: Set<LauncherSection>
    )
}