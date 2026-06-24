package off.kys.openarcade.ui.launcher.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import off.kys.openarcade.R
import off.kys.openarcade.domain.model.LauncherSection
import off.kys.openarcade.ui.components.ArcadeBottomSheet
import off.kys.openarcade.ui.components.ArcadeToggle
import off.kys.openarcade.ui.components.rememberArcadeSheetState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectionEditBottomSheet(
    visibleSections: Set<LauncherSection>,
    onDismissRequest: () -> Unit,
    onToggleSection: (LauncherSection, Boolean) -> Unit,
    sheetState: SheetState = rememberArcadeSheetState()
) {
    ArcadeBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        title = stringResource(R.string.customize_home),
        subtitle = stringResource(R.string.customize_home_subtitle)
    ) {
        LauncherSection.entries.forEachIndexed { index, section ->
            SectionToggleItem(
                section = section,
                isVisible = section in visibleSections,
                onToggle = { onToggleSection(section, it) }
            )

            if (index < LauncherSection.entries.lastIndex) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.06f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
        }

        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun SectionToggleItem(
    section: LauncherSection,
    isVisible: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary

    val title = when (section) {
        LauncherSection.SYSTEM_STATUS -> stringResource(R.string.system_status)
        LauncherSection.ANALYTICS -> stringResource(R.string.analytics)
        LauncherSection.FAVORITES -> stringResource(R.string.favorites)
        LauncherSection.RECENT_ACTIVITY -> stringResource(R.string.recent_activity)
    }
    val iconRes = when (section) {
        LauncherSection.SYSTEM_STATUS -> R.drawable.round_bolt_24
        LauncherSection.ANALYTICS -> R.drawable.round_bar_chart_24
        LauncherSection.FAVORITES -> R.drawable.round_favorite_24
        LauncherSection.RECENT_ACTIVITY -> R.drawable.round_history_24
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Row background wash — same alpha recipe as GameActionItem
    val rowBackground by animateColorAsState(
        targetValue = when {
            isPressed && isVisible -> primary.copy(alpha = 0.14f)
            isPressed -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
            else -> Color.Transparent
        },
        animationSpec = tween(180),
        label = "rowBg_${section.name}"
    )

    val rowBorderBrush = Brush.linearGradient(
        listOf(
            if (isVisible) primary.copy(alpha = if (isPressed) 0.45f else 0.18f)
            else tertiary.copy(alpha = if (isPressed) 0.25f else 0.08f),
            Color.Transparent
        )
    )

    // Icon badge colors — mirrors GameActionItem icon badge
    val iconBadgeBackground by animateColorAsState(
        targetValue = if (isVisible) primary.copy(alpha = 0.18f)
        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
        animationSpec = tween(200),
        label = "iconBadgeBg_${section.name}"
    )
    val iconTint by animateColorAsState(
        targetValue = if (isVisible) primary
        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
        animationSpec = tween(200),
        label = "iconTint_${section.name}"
    )
    val labelColor by animateColorAsState(
        targetValue = if (isVisible) MaterialTheme.colorScheme.onSurface
        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.50f),
        animationSpec = tween(200),
        label = "labelColor_${section.name}"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(rowBackground, MaterialTheme.shapes.medium)
            .border(1.dp, rowBorderBrush, MaterialTheme.shapes.medium)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onToggle(!isVisible) }
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(MaterialTheme.shapes.small)
                .background(
                    Brush.radialGradient(
                        listOf(
                            iconBadgeBackground,
                            iconBadgeBackground.copy(alpha = iconBadgeBackground.alpha * 0.25f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            iconTint.copy(alpha = 0.45f),
                            Color.Transparent
                        )
                    ),
                    shape = MaterialTheme.shapes.small
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
        }

        // Label
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (isVisible) FontWeight.SemiBold else FontWeight.Normal
            ),
            color = labelColor,
            modifier = Modifier.weight(1f)
        )

        // Custom arcade toggle — replaces Switch
        ArcadeToggle(
            checked = isVisible,
            onCheckedChange = onToggle
        )
    }
}