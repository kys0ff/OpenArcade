package off.kys.openarcade.ui.launcher

import off.kys.openarcade.domain.model.GameEntry
import off.kys.openarcade.domain.model.GameFilter
import off.kys.openarcade.domain.model.GameSortOption
import off.kys.openarcade.domain.model.LauncherSection

data class GamesLauncherUiState(
    val filteredGames: List<GameEntry> = emptyList(),
    val favoriteGames: List<GameEntry> = emptyList(),
    val recentGames: List<GameEntry> = emptyList(),
    val filters: List<GameFilter> = listOf(GameFilter.All),
    val selectedFilter: GameFilter = GameFilter.All,
    val selectedSort: GameSortOption = GameSortOption.TITLE_ASC,
    val visibleSections: Set<LauncherSection> = LauncherSection.entries.toSet(),
    val batteryLevel: Int = 0,
    val storageUsage: Int = 0,
    val hasUsageStatsPermission: Boolean = true,
    val isLoading: Boolean = true
)