package off.kys.openarcade.ui.launcher.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import off.kys.openarcade.R
import off.kys.openarcade.domain.model.GameCategory
import off.kys.openarcade.domain.model.GameFilter
import off.kys.openarcade.ui.launcher.GamesLauncherUiState
import off.kys.openarcade.ui.theme.OpenArcadeTheme


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

            ArcadeFilterChip(
                label = when (filter) {
                    is GameFilter.All -> stringResource(R.string.filter_all)
                    is GameFilter.Installed -> stringResource(R.string.category_installed)
                    is GameFilter.Uninstalled -> stringResource(R.string.category_uninstalled)
                    is GameFilter.System -> stringResource(filter.category.displayNameRes)
                    is GameFilter.Custom -> filter.name
                },
                selected = uiState.selectedFilter == filter,
                onClick = { onFilterSelected(filter) }
            )
        }
    }
}

private val ChipHeight = 32.dp
private val ChipHorizontalPadding = 12.dp
private val ChipIconSize = 16.dp
private val ChipIconSpacing = 4.dp
private const val AnimDuration = 200

@Composable
private fun ArcadeFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val surfaceContainerLow = MaterialTheme.colorScheme.surfaceContainerLow
    val onPrimaryContainer = MaterialTheme.colorScheme.onPrimaryContainer
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    // Animate background fill
    val containerColor by animateColorAsState(
        targetValue = if (selected) primaryContainer.copy(alpha = 0.72f) else surfaceContainerLow,
        animationSpec = tween(AnimDuration),
        label = "chipContainer"
    )

    val labelColor by animateColorAsState(
        targetValue = if (selected) onPrimaryContainer else onSurfaceVariant,
        animationSpec = tween(AnimDuration),
        label = "chipLabel"
    )

    val iconTint by animateColorAsState(
        targetValue = if (selected) primary else Color.Transparent,
        animationSpec = tween(AnimDuration),
        label = "chipIcon"
    )

    // Gradient border: stronger when selected
    val borderBrush = if (selected) {
        Brush.linearGradient(
            listOf(
                primary.copy(alpha = 0.85f),
                tertiary.copy(alpha = 0.40f),
                Color.Transparent
            )
        )
    } else {
        Brush.linearGradient(
            listOf(
                tertiary.copy(alpha = 0.30f),
                Color.Transparent
            )
        )
    }

    // Animate leading slot width so the icon slides in/out without a layout jump
    val leadingSlotWidth by animateDpAsState(
        targetValue = if (selected) ChipIconSize + ChipIconSpacing else 0.dp,
        animationSpec = tween(AnimDuration),
        label = "chipLeadingSlot"
    )

    Box(
        modifier = modifier
            .height(ChipHeight)
            .clip(CircleShape)
            .background(containerColor, CircleShape)
            .border(width = 1.dp, brush = borderBrush, shape = CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = ChipHorizontalPadding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Leading icon slot — collapses when not selected
            if (leadingSlotWidth > 0.dp) {
                Icon(
                    painter = painterResource(R.drawable.round_check_24),
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(ChipIconSize)
                )
                Spacer(Modifier.width(ChipIconSpacing))
            }

            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
                ),
                color = labelColor,
                maxLines = 1
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilterChipsRowPreview() {
    OpenArcadeTheme {
        FilterChipsRow(
            uiState = GamesLauncherUiState(
                filters = listOf(
                    GameFilter.All,
                    GameFilter.Installed,
                    GameFilter.Uninstalled,
                    GameFilter.System(GameCategory.GAME),
                    GameFilter.System(GameCategory.UTILITY),
                    GameFilter.Custom("Favorites"),
                    GameFilter.Custom("Action")
                ),
                selectedFilter = GameFilter.All
            ),
            onFilterSelected = {}
        )
    }
}