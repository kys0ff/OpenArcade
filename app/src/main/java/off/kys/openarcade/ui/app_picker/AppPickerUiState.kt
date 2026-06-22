package off.kys.openarcade.ui.app_picker

data class AppPickerUiState(
    val apps: List<AppInfo> = emptyList(),
    val trackedPackages: Map<String, Boolean> = emptyMap(), // packageName to isHidden
    val searchQuery: String = "",
    val selectedPackages: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val isDone: Boolean = false
) {
    val filteredApps: List<AppInfo>
        get() = apps.filter { app ->
            val isTracked = trackedPackages.containsKey(app.packageName)
            val isHidden = trackedPackages[app.packageName] ?: false

            // Show if not tracked OR if tracked but hidden (excluded)
            val shouldShow = !isTracked || isHidden

            shouldShow && (
                    app.label.contains(searchQuery, ignoreCase = true) ||
                            app.packageName.contains(searchQuery, ignoreCase = true)
                    )
        }
}