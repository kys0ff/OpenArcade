package off.kys.openarcade.ui.detail

import off.kys.openarcade.domain.model.GameEntry

data class GameDetailUiState(
    val game: GameEntry? = null,
    val showCategoryDialog: Boolean = false,
    val editingCategories: List<String> = emptyList(),
    val newCategoryDraft: String = ""
)
