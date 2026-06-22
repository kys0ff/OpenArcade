package off.kys.openarcade.domain.model

sealed class GameFilter {
    data object All : GameFilter()
    data object Installed : GameFilter()
    data object Uninstalled : GameFilter()
    data class System(val category: GameCategory) : GameFilter()
    data class Custom(val name: String) : GameFilter()
    data object Hidden : GameFilter()
}