package off.kys.openarcade.ui.launcher.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import off.kys.openarcade.R
import off.kys.openarcade.domain.model.GameFilter
import off.kys.openarcade.ui.launcher.GamesLauncherUiState

@Composable
fun FilterChipsRow(
    uiState: GamesLauncherUiState,
    onFilterSelected: (GameFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        items(
            count = uiState.filters.size,
            key = { index ->
                when (val filter = uiState.filters[index]) {
                    is GameFilter.All -> "all"
                    is GameFilter.Installed -> "installed"
                    is GameFilter.Uninstalled -> "uninstalled"
                    is GameFilter.System -> "system_${filter.category.name}"
                    is GameFilter.Custom -> "custom_${filter.name}"
                }
            }
        ) { index ->
            val filter = uiState.filters[index]
            val isSelected = uiState.selectedFilter == filter

            FilterChip(
                selected = isSelected,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(
                        text = when (filter) {
                            is GameFilter.All -> stringResource(R.string.filter_all)
                            is GameFilter.Installed -> stringResource(R.string.category_installed)
                            is GameFilter.Uninstalled -> stringResource(R.string.category_uninstalled)
                            is GameFilter.System -> stringResource(filter.category.displayNameRes)
                            is GameFilter.Custom -> filter.name
                        },
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    )
                },
                leadingIcon = if (isSelected) {
                    {
                        Icon(
                            painter = painterResource(R.drawable.round_check_24),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else null,
                shape = CircleShape,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = Color.Transparent,
                    selectedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    disabledSelectedBorderColor = Color.Transparent
                )
            )
        }
    }
}