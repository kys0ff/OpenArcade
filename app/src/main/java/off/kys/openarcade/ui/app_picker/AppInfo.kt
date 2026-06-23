package off.kys.openarcade.ui.app_picker

import androidx.compose.runtime.Immutable

@Immutable
data class AppInfo(
    val packageName: String,
    val label: String
)
