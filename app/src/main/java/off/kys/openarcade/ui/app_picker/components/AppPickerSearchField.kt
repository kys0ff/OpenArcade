package off.kys.openarcade.ui.app_picker.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import off.kys.openarcade.R
import off.kys.openarcade.ui.components.ArcadeSearchField

@Composable
fun AppPickerSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ArcadeSearchField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 12.dp),
        placeholder = stringResource(R.string.search_apps)
    )
}
