package off.kys.openarcade.ui.detail

import android.app.Application
import android.content.Intent
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import off.kys.openarcade.data.local.ArcadePreferences
import off.kys.openarcade.domain.usecase.GetGameByPackageUseCase
import off.kys.openarcade.domain.usecase.RefreshGameStatsUseCase
import off.kys.openarcade.domain.usecase.UpdateGameCategoryUseCase

class GameDetailViewModel(
    private val packageName: String,
    application: Application,
    getGameByPackageUseCase: GetGameByPackageUseCase,
    private val updateGameCategoryUseCase: UpdateGameCategoryUseCase,
    private val refreshGameStatsUseCase: RefreshGameStatsUseCase,
    prefs: ArcadePreferences
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(GameDetailUiState())
    val uiState: StateFlow<GameDetailUiState> = combine(
        getGameByPackageUseCase(packageName),
        prefs.showScrollbar,
        prefs.hapticFeedback,
        _uiState
    ) { game, showScrollbar, haptic, state ->
        state.copy(
            game = game,
            showScrollbar = showScrollbar,
            hapticFeedback = haptic
        )
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
        val game = uiState.value.game ?: return

        if (game.isInstalled) {
            val launchIntent = application.packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                application.startActivity(launchIntent)
            }
        } else {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = "market://details?id=$packageName".toUri()
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                application.startActivity(intent)
            } catch (_: Exception) {
                val webIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = "https://play.google.com/store/apps/details?id=$packageName".toUri()
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                application.startActivity(webIntent)
            }
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
