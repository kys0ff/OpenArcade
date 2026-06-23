package off.kys.openarcade.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import off.kys.openarcade.domain.model.GameSortOption
import off.kys.openarcade.domain.model.LauncherSection

class ArcadePreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _selectedSort = MutableStateFlow(loadSortOption())
    val selectedSort: StateFlow<GameSortOption> = _selectedSort.asStateFlow()

    private val _visibleSections = MutableStateFlow(loadVisibleSections())
    val visibleSections: StateFlow<Set<LauncherSection>> = _visibleSections.asStateFlow()

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

    companion object {
        private const val PREFS_NAME = "arcade_prefs"
        private const val KEY_SORT_OPTION = "selected_sort_option"
        private const val KEY_VISIBLE_SECTIONS = "visible_sections"
    }
}
