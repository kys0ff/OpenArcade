package off.kys.openarcade.domain.usecase

import off.kys.openarcade.domain.repository.GameRepository

class UpdateGameCategoryUseCase(private val repository: GameRepository) {
    suspend operator fun invoke(packageName: String, customCategories: List<String>) =
        repository.updateCustomCategories(packageName, customCategories)
}
