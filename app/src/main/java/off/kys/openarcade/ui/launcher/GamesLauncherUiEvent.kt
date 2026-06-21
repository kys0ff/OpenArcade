package off.kys.openarcade.ui.launcher

import off.kys.openarcade.domain.model.GameFilter

sealed interface GamesLauncherUiEvent {
    data class FilterSelected(val filter: GameFilter) : GamesLauncherUiEvent
    data class GameClicked(val packageName: String) : GamesLauncherUiEvent
    data object GrantPermissionClicked : GamesLauncherUiEvent
    data object RefreshRequested : GamesLauncherUiEvent
    data object PermissionCheckRequested : GamesLauncherUiEvent
}