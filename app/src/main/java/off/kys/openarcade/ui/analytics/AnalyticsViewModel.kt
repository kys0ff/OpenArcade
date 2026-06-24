package off.kys.openarcade.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import off.kys.openarcade.data.local.ArcadePreferences
import off.kys.openarcade.domain.usecase.GetAnalyticsDataUseCase

class AnalyticsViewModel(
    getAnalyticsDataUseCase: GetAnalyticsDataUseCase,
    private val prefs: ArcadePreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    private val analyticsData = getAnalyticsDataUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    init {
        viewModelScope.launch {
            analyticsData.collect { data ->
                _uiState.update { it.copy(isLoading = data == null, data = data) }
            }
        }
        viewModelScope.launch {
            prefs.showScrollbar.collect { show ->
                _uiState.update { it.copy(showScrollbar = show) }
            }
        }
    }

    fun onEvent(event: AnalyticsUiEvent) {
        when (event) {
            is AnalyticsUiEvent.RefreshRequested -> {
                // Flow-based data will refresh when active
            }
            is AnalyticsUiEvent.IntervalSelected -> {
                _uiState.update { it.copy(selectedInterval = event.interval) }
            }
            is AnalyticsUiEvent.BackClicked -> { /* Handled by UI */ }
        }
    }
}
