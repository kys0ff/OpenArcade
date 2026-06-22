package off.kys.openarcade.ui.app_picker

import android.app.Application
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import off.kys.openarcade.domain.model.GameCategory
import off.kys.openarcade.domain.model.GameEntry
import off.kys.openarcade.domain.repository.GameRepository
import off.kys.openarcade.domain.usecase.RefreshGamesUseCase

class AppPickerViewModel(
    private val application: Application,
    private val gameRepository: GameRepository,
    private val refreshGamesUseCase: RefreshGamesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppPickerUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        loadApps()
        observeTrackedGames()
    }

    private fun observeTrackedGames() {
        viewModelScope.launch {
            gameRepository.getGames().collect { games ->
                val tracked = games.associate { it.packageName to it.isHidden }
                _uiState.update { it.copy(trackedPackages = tracked) }
            }
        }
    }

    private fun loadApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val pm = application.packageManager
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val apps = pm.queryIntentActivities(intent, 0).map {
                AppInfo(
                    packageName = it.activityInfo.packageName,
                    label = it.loadLabel(pm).toString(),
                    icon = it.loadIcon(pm)
                )
            }.sortedBy { it.label.lowercase() }

            _uiState.update { it.copy(apps = apps, isLoading = false) }
        }
    }

    fun onEvent(event: AppPickerUiEvent) {
        when (event) {
            is AppPickerUiEvent.SearchQueryChanged -> {
                _uiState.update { it.copy(searchQuery = event.query) }
            }
            is AppPickerUiEvent.ToggleAppSelection -> {
                _uiState.update { state ->
                    val newSelection = state.selectedPackages.toMutableSet()
                    if (newSelection.contains(event.packageName)) {
                        newSelection.remove(event.packageName)
                    } else {
                        newSelection.add(event.packageName)
                    }
                    state.copy(selectedPackages = newSelection)
                }
            }
            AppPickerUiEvent.AddSelectedApps -> {
                addSelectedApps()
            }
        }
    }

    private fun addSelectedApps() {
        val selected = _uiState.value.selectedPackages
        if (selected.isEmpty()) return

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            selected.forEach { pkg ->
                val isTracked = _uiState.value.trackedPackages.containsKey(pkg)
                if (isTracked) {
                    gameRepository.updateVisibility(pkg, false)
                } else {
                    gameRepository.addGame(
                        GameEntry(
                            packageName = pkg,
                            title = "", // Will be updated by scanner
                            category = GameCategory.UTILITY,
                            primaryColorArgb = 0,
                            onPrimaryColorArgb = 0,
                            secondaryColorArgb = 0,
                            tertiaryColorArgb = 0,
                            isManuallyAdded = true
                        )
                    )
                }
            }
            refreshGamesUseCase()
            _uiState.update { it.copy(isDone = true, isLoading = false) }
        }
    }
}
