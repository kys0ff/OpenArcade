package off.kys.openarcade.ui.launcher.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
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


private val ChipHeight = 32.dp
private val ChipHorizontalPadding = 12.dp
private val ChipIconSize = 16.dp
private val ChipIconSpacing = 4.dp
private const val AnimDuration = 200

private val TrailingClusterWidth = 80.dp
private val ScrimWidth = 48.dp


@Composable
fun FilterChipsRow(
    modifier: Modifier = Modifier,
    uiState: GamesLauncherUiState,
    onFilterSelected: (GameFilter) -> Unit,
    onSettingsClick: () -> Unit,
    onSectionsEdit: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val background = MaterialTheme.colorScheme.background

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(ChipHeight + 16.dp)
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(
                start = 0.dp,
                end = TrailingClusterWidth,
                top = 8.dp,
                bottom = 8.dp
            ),
            modifier = Modifier.fillMaxWidth()
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
                        is GameFilter.Hidden -> "hidden"
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
                        is GameFilter.Hidden -> stringResource(R.string.filter_hidden)
                    },
                    selected = uiState.selectedFilter == filter,
                    onClick = {
                        if (uiState.hapticFeedback) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                        onFilterSelected(filter)
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(TrailingClusterWidth + ScrimWidth)
                .fillMaxHeight()
                .background(
                    Brush.horizontalGradient(
                        0.00f to Color.Transparent,
                        0.45f to background.copy(alpha = 0.85f),
                        1.00f to background
                    )
                )
        )

        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ChipActionButton(
                iconRes = R.drawable.round_settings_24,
                contentDescription = "Settings",
                onClick = {
                    if (uiState.hapticFeedback) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    onSettingsClick()
                }
            )

            ChipActionButton(
                iconRes = R.drawable.round_category_24,
                contentDescription = "Edit sections",
                onClick = {
                    if (uiState.hapticFeedback) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    onSectionsEdit()
                },
                isPrimary = false
            )
        }
    }
}

@Composable
private fun ChipActionButton(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    isPrimary: Boolean = true
) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val containerColor by animateColorAsState(
        targetValue = when {
            isPrimary -> MaterialTheme.colorScheme.primaryContainer.copy(
                alpha = if (isPressed) 0.90f else 0.72f
            )

            else -> MaterialTheme.colorScheme.surfaceContainerLow
        },
        animationSpec = tween(AnimDuration),
        label = "actionBtnContainer"
    )

    val iconTint by animateColorAsState(
        targetValue = if (isPrimary) {
            if (isPressed) primary else MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            if (isPressed) primary else MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(AnimDuration),
        label = "actionBtnIcon"
    )

    val borderBrush = if (isPrimary) {
        Brush.linearGradient(
            listOf(
                primary.copy(alpha = if (isPressed) 1.00f else 0.85f),
                tertiary.copy(alpha = if (isPressed) 0.65f else 0.40f),
                Color.Transparent
            )
        )
    } else {
        Brush.linearGradient(
            listOf(
                tertiary.copy(alpha = if (isPressed) 0.55f else 0.30f),
                Color.Transparent
            )
        )
    }

    Box(
        modifier = Modifier
            .size(ChipHeight)
            .clip(CircleShape)
            .background(containerColor, CircleShape)
            .border(1.dp, borderBrush, CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun ArcadeFilterChip(
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

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val containerColor by animateColorAsState(
        targetValue = when {
            selected -> primaryContainer.copy(alpha = if (isPressed) 0.85f else 0.72f)
            else -> if (isPressed) surfaceContainerLow.copy(alpha = 0.8f) else surfaceContainerLow
        },
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

    val borderBrush = if (selected) {
        Brush.linearGradient(
            listOf(
                primary.copy(alpha = if (isPressed) 1.00f else 0.85f),
                tertiary.copy(alpha = if (isPressed) 0.65f else 0.40f),
                Color.Transparent
            )
        )
    } else {
        Brush.linearGradient(
            listOf(
                tertiary.copy(alpha = if (isPressed) 0.55f else 0.30f),
                Color.Transparent
            )
        )
    }

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
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = ChipHorizontalPadding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .width(leadingSlotWidth)
                    .clipToBounds(),
                contentAlignment = Alignment.CenterStart
            ) {
                this@Row.AnimatedVisibility(
                    visible = selected,
                    enter = fadeIn(tween(AnimDuration)),
                    exit = fadeOut(tween(AnimDuration))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.round_check_24),
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(ChipIconSize)
                        )
                        Spacer(Modifier.width(ChipIconSpacing))
                    }
                }
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
                    GameFilter.System(GameCategory.UTILITY),
                    GameFilter.Custom("Favorites"),
                    GameFilter.Custom("Action")
                ),
                selectedFilter = GameFilter.All
            ),
            onFilterSelected = {},
            onSettingsClick = {},
            onSectionsEdit = {}
        )
    }
}