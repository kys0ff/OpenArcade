package off.kys.openarcade.ui.launcher

import off.kys.openarcade.domain.model.GameFilter
import off.kys.openarcade.domain.model.GameSortOption

sealed interface GamesLauncherUiEvent {
    data class FilterSelected(val filter: GameFilter) : GamesLauncherUiEvent
    data class SortSelected(val sort: GameSortOption) : GamesLauncherUiEvent
    data class GameClicked(val packageName: String) : GamesLauncherUiEvent
    data object GrantPermissionClicked : GamesLauncherUiEvent
    data object RefreshRequested : GamesLauncherUiEvent
    data object PermissionCheckRequested : GamesLauncherUiEvent
    data object RefreshStats : GamesLauncherUiEvent
}