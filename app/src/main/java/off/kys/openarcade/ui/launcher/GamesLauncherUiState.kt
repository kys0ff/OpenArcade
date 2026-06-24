package off.kys.openarcade.ui.launcher

import off.kys.openarcade.domain.model.GameEntry
import off.kys.openarcade.domain.model.GameFilter
import off.kys.openarcade.domain.model.GameSortOption
import off.kys.openarcade.domain.model.LauncherSection
import off.kys.openarcade.ui.settings.GridColumns
import off.kys.openarcade.ui.settings.ScreenOrientation

data class GamesLauncherUiState(
    val filteredGames: List<GameEntry> = emptyList(),
    val favoriteGames: List<GameEntry> = emptyList(),
    val recentGames: List<GameEntry> = emptyList(),
    val filters: List<GameFilter> = listOf(GameFilter.All),
    val selectedFilter: GameFilter = GameFilter.All,
    val selectedSort: GameSortOption = GameSortOption.TITLE_ASC,
    val visibleSections: Set<LauncherSection> = LauncherSection.entries.toSet(),
    val gridColumns: GridColumns = GridColumns.Three,
    val screenOrientation: ScreenOrientation = ScreenOrientation.Auto,
    val immersiveMode: Boolean = false,
    val keepScreenOn: Boolean = false,
    val showScrollbar: Boolean = true,
    val hapticFeedback: Boolean = true,
    val batteryLevel: Int = 0,
    val storageUsage: Int = 0,
    val hasUsageStatsPermission: Boolean = true,
    val isLoading: Boolean = true
)