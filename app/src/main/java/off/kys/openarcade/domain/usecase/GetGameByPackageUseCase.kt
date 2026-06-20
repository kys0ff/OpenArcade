package off.kys.openarcade.domain.usecase

import kotlinx.coroutines.flow.Flow
import off.kys.openarcade.domain.model.GameEntry
import off.kys.openarcade.domain.repository.GameRepository

class GetGameByPackageUseCase(private val repository: GameRepository) {
    operator fun invoke(packageName: String): Flow<GameEntry?> = 
        repository.getGameByPackageName(packageName)
}
