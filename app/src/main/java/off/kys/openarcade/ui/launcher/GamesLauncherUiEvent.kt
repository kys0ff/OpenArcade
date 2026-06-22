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

    data class FavoriteToggled(val packageName: String, val isFavorite: Boolean) : GamesLauncherUiEvent
    data class VisibilityToggled(val packageName: String, val isHidden: Boolean) : GamesLauncherUiEvent
    data class RenameRequested(val packageName: String, val newTitle: String?) : GamesLauncherUiEvent
    data class ChangeIconRequested(val packageName: String, val newIconPath: String?) : GamesLauncherUiEvent
    data class AppInfoRequested(val packageName: String) : GamesLauncherUiEvent
    data class UninstallRequested(val packageName: String) : GamesLauncherUiEvent
    data class AddGamesRequested(val packageNames: List<String>) : GamesLauncherUiEvent
}