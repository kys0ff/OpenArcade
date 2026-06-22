package off.kys.openarcade.domain.usecase

import off.kys.openarcade.domain.repository.GameRepository

class RefreshGameStatsUseCase(private val repository: GameRepository) {
    suspend operator fun invoke(packageName: String) =
        repository.refreshGameStats(packageName)
}
