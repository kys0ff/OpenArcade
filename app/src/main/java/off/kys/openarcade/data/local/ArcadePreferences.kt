package off.kys.openarcade.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import off.kys.openarcade.domain.model.GameSortOption

class ArcadePreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _selectedSort = MutableStateFlow(loadSortOption())
    val selectedSort: StateFlow<GameSortOption> = _selectedSort.asStateFlow()

    fun setSortOption(option: GameSortOption) {
        prefs.edit { putString(KEY_SORT_OPTION, option.name) }
        _selectedSort.value = option
    }

    private fun loadSortOption(): GameSortOption {
        val name = prefs.getString(KEY_SORT_OPTION, GameSortOption.TITLE_ASC.name)
        return try {
            GameSortOption.valueOf(name ?: GameSortOption.TITLE_ASC.name)
        } catch (_: Exception) {
            GameSortOption.TITLE_ASC
        }
    }

    companion object {
        private const val PREFS_NAME = "arcade_prefs"
        private const val KEY_SORT_OPTION = "selected_sort_option"
    }
}
