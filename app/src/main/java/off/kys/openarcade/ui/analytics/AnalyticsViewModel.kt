package off.kys.openarcade.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import off.kys.openarcade.domain.usecase.GetAnalyticsDataUseCase

class AnalyticsViewModel(
    private val getAnalyticsDataUseCase: GetAnalyticsDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadAnalyticsData()
    }

    private fun loadAnalyticsData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getAnalyticsDataUseCase().collect { data ->
                _uiState.update { it.copy(isLoading = false, data = data) }
            }
        }
    }

    fun onEvent(event: AnalyticsUiEvent) {
        when (event) {
            is AnalyticsUiEvent.RefreshRequested -> loadAnalyticsData()
            is AnalyticsUiEvent.IntervalSelected -> {
                _uiState.update { it.copy(selectedInterval = event.interval) }
            }
            is AnalyticsUiEvent.BackClicked -> { /* Handled by UI */ }
        }
    }
}
