package off.kys.openarcade.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class AnalyticsData(
    val totalPlayTimeMs: Long,
    val dailyTrend: List<PlayTimePoint>,
    val weeklyTrend: List<PlayTimePoint>,
    val categoryDistribution: List<CategoryDistribution>,
    val topGames: List<TopGame>
)

@Immutable
data class PlayTimePoint(
    val label: String,
    val playTimeMs: Long,
    val timestamp: Long
)

@Immutable
data class CategoryDistribution(
    val category: GameCategory,
    val playTimeMs: Long,
    val percentage: Float
)

@Immutable
data class TopGame(
    val packageName: String,
    val title: String,
    val playTimeMs: Long,
    val primaryColorArgb: Int
)
