package off.kys.openarcade.domain.usecase

import off.kys.openarcade.domain.repository.GameRepository

class RefreshGamesUseCase(private val repository: GameRepository) {
    suspend operator fun invoke() = repository.refreshGames()
}
