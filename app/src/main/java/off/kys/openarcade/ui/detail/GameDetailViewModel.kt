package off.kys.openarcade.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import off.kys.openarcade.domain.usecase.GetGameByPackageUseCase
import off.kys.openarcade.domain.usecase.RefreshGameStatsUseCase
import off.kys.openarcade.domain.usecase.UpdateGameCategoryUseCase

class GameDetailViewModel(
    private val packageName: String,
    application: Application,
    getGameByPackageUseCase: GetGameByPackageUseCase,
    private val updateGameCategoryUseCase: UpdateGameCategoryUseCase,
    private val refreshGameStatsUseCase: RefreshGameStatsUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(GameDetailUiState())
    val uiState: StateFlow<GameDetailUiState> = combine(
        getGameByPackageUseCase(packageName),
        _uiState
    ) { game, state ->
        state.copy(game = game)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GameDetailUiState()
    )

    fun onEvent(event: GameDetailUiEvent) {
        when (event) {
            is GameDetailUiEvent.LaunchGame -> launchGame()
            is GameDetailUiEvent.RefreshStats -> refreshStats()
            is GameDetailUiEvent.OpenCategoryDialog -> {
                val currentCategories = uiState.value.game?.customCategories ?: emptyList()
                _uiState.update {
                    it.copy(
                        showCategoryDialog = true,
                        editingCategories = currentCategories,
                        newCategoryDraft = ""
                    )
                }
            }

            is GameDetailUiEvent.CloseCategoryDialog -> _uiState.update { it.copy(showCategoryDialog = false) }
            is GameDetailUiEvent.AddCategory -> _uiState.update { state ->
                val trimmed = event.category.trim()
                if (trimmed.isNotBlank() && trimmed !in state.editingCategories) {
                    state.copy(
                        editingCategories = state.editingCategories + trimmed,
                        newCategoryDraft = ""
                    )
                } else state
            }

            is GameDetailUiEvent.RemoveCategory -> _uiState.update { state ->
                state.copy(
                    editingCategories = state.editingCategories - event.category
                )
            }

            is GameDetailUiEvent.UpdateNewCategoryDraft -> _uiState.update {
                it.copy(
                    newCategoryDraft = event.text
                )
            }

            is GameDetailUiEvent.SaveCategories -> saveCategories()
        }
    }

    private fun launchGame() {
        val application = getApplication<Application>()
        val launchIntent = application.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            application.startActivity(launchIntent)
        }
    }

    private fun refreshStats() {
        viewModelScope.launch {
            refreshGameStatsUseCase(packageName)
        }
    }

    private fun saveCategories() {
        viewModelScope.launch {
            updateGameCategoryUseCase(packageName, _uiState.value.editingCategories)
            _uiState.update { it.copy(showCategoryDialog = false) }
        }
    }
}
