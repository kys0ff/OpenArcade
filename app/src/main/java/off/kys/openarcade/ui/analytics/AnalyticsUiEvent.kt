package off.kys.openarcade.ui.analytics

sealed class AnalyticsUiEvent {
    data object RefreshRequested : AnalyticsUiEvent()
    data class IntervalSelected(val interval: AnalyticsInterval) : AnalyticsUiEvent()
    data object BackClicked : AnalyticsUiEvent()
}
