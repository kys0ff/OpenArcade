package off.kys.openarcade.ui.launcher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import off.kys.openarcade.domain.model.GameCategory
import off.kys.openarcade.domain.model.GameEntry
import off.kys.openarcade.domain.usecase.GetGamesUseCase
import off.kys.openarcade.domain.usecase.RefreshGamesUseCase

sealed class GameFilter {
    data object All : GameFilter()
    data object Installed : GameFilter()
    data object Uninstalled : GameFilter()
    data class System(val category: GameCategory) : GameFilter()
    data class Custom(val name: String) : GameFilter()
}

class GamesLauncherViewModel(
    private val refreshGamesUseCase: RefreshGamesUseCase,
    getGamesUseCase: GetGamesUseCase
) : ViewModel() {

    val allGames: StateFlow<List<GameEntry>> = getGamesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val availableFilters: StateFlow<List<GameFilter>> = allGames.map { games ->
        val filters = mutableListOf<GameFilter>()
        if (games.isNotEmpty()) {
            filters.add(GameFilter.All)
            filters.add(GameFilter.Installed)
            if (games.any { !it.isInstalled }) {
                filters.add(GameFilter.Uninstalled)
            }
        }

        GameCategory.entries.forEach { category ->
            if (category != GameCategory.UNDEFINED && games.any { it.category == category }) {
                filters.add(GameFilter.System(category))
            }
        }

        val custom = games.flatMap { it.customCategories }.distinct().sorted()
        custom.forEach { filters.add(GameFilter.Custom(it)) }

        filters
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = listOf(GameFilter.All)
    )

    init {
        refreshGames()
    }

    fun refreshGames() {
        viewModelScope.launch {
            refreshGamesUseCase()
        }
    }
}
