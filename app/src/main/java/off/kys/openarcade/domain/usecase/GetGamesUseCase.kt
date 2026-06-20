package off.kys.openarcade.domain.usecase

import kotlinx.coroutines.flow.Flow
import off.kys.openarcade.domain.model.GameEntry
import off.kys.openarcade.domain.repository.GameRepository

class GetGamesUseCase(private val repository: GameRepository) {
    operator fun invoke(): Flow<List<GameEntry>> = repository.getGames()
}
