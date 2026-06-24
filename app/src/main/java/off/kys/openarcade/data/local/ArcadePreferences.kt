package off.kys.openarcade.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import off.kys.openarcade.domain.model.GameSortOption
import off.kys.openarcade.domain.model.LauncherSection
import off.kys.openarcade.ui.settings.GridColumns
import off.kys.openarcade.ui.settings.ScreenOrientation

class ArcadePreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _selectedSort = MutableStateFlow(loadSortOption())
    val selectedSort: StateFlow<GameSortOption> = _selectedSort.asStateFlow()

    private val _visibleSections = MutableStateFlow(loadVisibleSections())
    val visibleSections: StateFlow<Set<LauncherSection>> = _visibleSections.asStateFlow()

    // --- New Settings ---
    private val _immersiveMode = MutableStateFlow(prefs.getBoolean(KEY_IMMERSIVE_MODE, false))
    val immersiveMode = _immersiveMode.asStateFlow()

    private val _keepScreenOn = MutableStateFlow(prefs.getBoolean(KEY_KEEP_SCREEN_ON, false))
    val keepScreenOn = _keepScreenOn.asStateFlow()

    private val _showScrollbar = MutableStateFlow(prefs.getBoolean(KEY_SHOW_SCROLLBAR, true))
    val showScrollbar = _showScrollbar.asStateFlow()

    private val _reduceAnimations = MutableStateFlow(prefs.getBoolean(KEY_REDUCE_ANIMATIONS, false))
    val reduceAnimations = _reduceAnimations.asStateFlow()

    private val _hapticFeedback = MutableStateFlow(prefs.getBoolean(KEY_HAPTIC_FEEDBACK, true))
    val hapticFeedback = _hapticFeedback.asStateFlow()

    private val _launchAnimation = MutableStateFlow(prefs.getBoolean(KEY_LAUNCH_ANIMATION, true))
    val launchAnimation = _launchAnimation.asStateFlow()

    private val _screenOrientation = MutableStateFlow(loadOrientation())
    val screenOrientation = _screenOrientation.asStateFlow()

    private val _gridColumns = MutableStateFlow(loadGridColumns())
    val gridColumns = _gridColumns.asStateFlow()


    fun setSortOption(option: GameSortOption) {
        prefs.edit { putString(KEY_SORT_OPTION, option.name) }
        _selectedSort.value = option
    }

    fun setSectionVisible(section: LauncherSection, visible: Boolean) {
        val current = _visibleSections.value.toMutableSet()
        if (visible) current.add(section) else current.remove(section)
        prefs.edit { putStringSet(KEY_VISIBLE_SECTIONS, current.map { it.name }.toSet()) }
        _visibleSections.value = current
    }

    fun setImmersiveMode(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_IMMERSIVE_MODE, enabled) }
        _immersiveMode.value = enabled
    }

    fun setKeepScreenOn(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_KEEP_SCREEN_ON, enabled) }
        _keepScreenOn.value = enabled
    }

    fun setShowScrollbar(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_SHOW_SCROLLBAR, enabled) }
        _showScrollbar.value = enabled
    }

    fun setReduceAnimations(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_REDUCE_ANIMATIONS, enabled) }
        _reduceAnimations.value = enabled
    }

    fun setHapticFeedback(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_HAPTIC_FEEDBACK, enabled) }
        _hapticFeedback.value = enabled
    }

    fun setLaunchAnimation(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_LAUNCH_ANIMATION, enabled) }
        _launchAnimation.value = enabled
    }

    fun setScreenOrientation(orientation: ScreenOrientation) {
        prefs.edit { putString(KEY_SCREEN_ORIENTATION, orientation.name) }
        _screenOrientation.value = orientation
    }

    fun setGridColumns(columns: GridColumns) {
        prefs.edit { putString(KEY_GRID_COLUMNS, columns.name) }
        _gridColumns.value = columns
    }

    fun resetAll() {
        prefs.edit { clear() }
        _immersiveMode.value = false
        _keepScreenOn.value = false
        _showScrollbar.value = true
        _reduceAnimations.value = false
        _hapticFeedback.value = true
        _launchAnimation.value = true
        _screenOrientation.value = ScreenOrientation.Auto
        _gridColumns.value = GridColumns.Three
        _visibleSections.value = LauncherSection.entries.toSet()
        _selectedSort.value = GameSortOption.TITLE_ASC
    }

    private fun loadSortOption(): GameSortOption {
        val name = prefs.getString(KEY_SORT_OPTION, GameSortOption.TITLE_ASC.name)
        return try {
            GameSortOption.valueOf(name ?: GameSortOption.TITLE_ASC.name)
        } catch (_: Exception) {
            GameSortOption.TITLE_ASC
        }
    }

    private fun loadVisibleSections(): Set<LauncherSection> {
        val names = prefs.getStringSet(KEY_VISIBLE_SECTIONS, LauncherSection.entries.map { it.name }.toSet())
        return names?.mapNotNull {
            try { LauncherSection.valueOf(it) } catch (_: Exception) { null }
        }?.toSet() ?: LauncherSection.entries.toSet()
    }

    private fun loadOrientation(): ScreenOrientation {
        val name = prefs.getString(KEY_SCREEN_ORIENTATION, ScreenOrientation.Auto.name)
        return try {
            ScreenOrientation.valueOf(name ?: ScreenOrientation.Auto.name)
        } catch (_: Exception) {
            ScreenOrientation.Auto
        }
    }

    private fun loadGridColumns(): GridColumns {
        val name = prefs.getString(KEY_GRID_COLUMNS, GridColumns.Three.name)
        return try {
            GridColumns.valueOf(name ?: GridColumns.Three.name)
        } catch (_: Exception) {
            GridColumns.Three
        }
    }

    companion object {
        private const val PREFS_NAME = "arcade_prefs"
        private const val KEY_SORT_OPTION = "selected_sort_option"
        private const val KEY_VISIBLE_SECTIONS = "visible_sections"

        private const val KEY_IMMERSIVE_MODE = "immersive_mode"
        private const val KEY_KEEP_SCREEN_ON = "keep_screen_on"
        private const val KEY_SHOW_SCROLLBAR = "show_scrollbar"
        private const val KEY_REDUCE_ANIMATIONS = "reduce_animations"
        private const val KEY_HAPTIC_FEEDBACK = "haptic_feedback"
        private const val KEY_LAUNCH_ANIMATION = "launch_animation"
        private const val KEY_SCREEN_ORIENTATION = "screen_orientation"
        private const val KEY_GRID_COLUMNS = "grid_columns"
    }
}
