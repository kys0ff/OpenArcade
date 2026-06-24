package off.kys.openarcade.ui.analytics

import off.kys.openarcade.domain.model.AnalyticsData

data class AnalyticsUiState(
    val isLoading: Boolean = true,
    val data: AnalyticsData? = null,
    val selectedInterval: AnalyticsInterval = AnalyticsInterval.DAILY,
    val showScrollbar: Boolean = true
)

enum class AnalyticsInterval {
    DAILY, WEEKLY
}
