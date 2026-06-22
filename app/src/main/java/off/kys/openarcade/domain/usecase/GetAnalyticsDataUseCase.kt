package off.kys.openarcade.domain.usecase

import off.kys.openarcade.domain.repository.GameRepository

class GetAnalyticsDataUseCase(private val repository: GameRepository) {
    operator fun invoke() = repository.getAnalyticsData()
}
