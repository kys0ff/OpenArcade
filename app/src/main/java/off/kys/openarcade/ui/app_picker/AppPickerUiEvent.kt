package off.kys.openarcade.ui.app_picker

sealed interface AppPickerUiEvent {
    data class SearchQueryChanged(val query: String) : AppPickerUiEvent
    data class ToggleAppSelection(val packageName: String) : AppPickerUiEvent
    data object AddSelectedApps : AppPickerUiEvent
}
