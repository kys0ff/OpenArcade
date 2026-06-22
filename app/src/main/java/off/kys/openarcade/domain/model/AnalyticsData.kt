package off.kys.openarcade.domain.model

data class AnalyticsData(
    val totalPlayTimeMs: Long,
    val dailyTrend: List<PlayTimePoint>,
    val weeklyTrend: List<PlayTimePoint>,
    val categoryDistribution: List<CategoryDistribution>,
    val topGames: List<TopGame>
)

data class PlayTimePoint(
    val label: String,
    val playTimeMs: Long,
    val timestamp: Long
)

data class CategoryDistribution(
    val category: GameCategory,
    val playTimeMs: Long,
    val percentage: Float
)

data class TopGame(
    val packageName: String,
    val title: String,
    val playTimeMs: Long,
    val primaryColorArgb: Int
)
