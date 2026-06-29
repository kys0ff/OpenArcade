package off.kys.openarcade.data.repository

import off.kys.openarcade.domain.model.GameEntry
import off.kys.openarcade.domain.repository.SystemRepository
import off.kys.openarcade.util.GameScanner

class SystemRepositoryImpl(
    private val gameScanner: GameScanner
) : SystemRepository {
    override fun fetchInstalledGames(extraPackages: List<String>): List<GameEntry> {
        return gameScanner.fetchInstalledGames(extraPackages)
    }
}
