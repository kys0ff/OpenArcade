package off.kys.openarcade.domain.repository

import off.kys.openarcade.domain.model.GameEntry

interface SystemRepository {
    fun fetchInstalledGames(extraPackages: List<String> = emptyList()): List<GameEntry>
}
