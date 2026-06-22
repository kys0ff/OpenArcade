package off.kys.openarcade.ui.detail

sealed interface GameDetailUiEvent {
    data object LaunchGame : GameDetailUiEvent
    data object OpenCategoryDialog : GameDetailUiEvent
    data object CloseCategoryDialog : GameDetailUiEvent
    data class AddCategory(val category: String) : GameDetailUiEvent
    data class RemoveCategory(val category: String) : GameDetailUiEvent
    data class UpdateNewCategoryDraft(val text: String) : GameDetailUiEvent
    data object SaveCategories : GameDetailUiEvent
    data object RefreshStats : GameDetailUiEvent
}
