package off.kys.openarcade.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun getDynamicColors(): List<Color> {
    val colorScheme = MaterialTheme.colorScheme

    return listOf(
        colorScheme.primary,
        colorScheme.secondary,
        colorScheme.inversePrimary,
        colorScheme.primaryContainer,
        colorScheme.tertiary,
        colorScheme.error
    )
}