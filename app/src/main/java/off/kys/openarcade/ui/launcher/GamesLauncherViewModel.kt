package off.kys.openarcade.ui.launcher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import off.kys.openarcade.domain.model.GameEntry
import off.kys.openarcade.domain.usecase.GetGamesUseCase
import off.kys.openarcade.domain.usecase.RefreshGamesUseCase

class GamesLauncherViewModel(
    private val refreshGamesUseCase: RefreshGamesUseCase,
    getGamesUseCase: GetGamesUseCase
) : ViewModel() {

    val installedGames: StateFlow<List<GameEntry>> = getGamesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
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
