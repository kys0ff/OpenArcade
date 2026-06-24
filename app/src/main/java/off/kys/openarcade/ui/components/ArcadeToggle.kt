package off.kys.openarcade.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp

/**
 * Pill-shaped toggle that replaces [Switch].
 * Track uses `horizontalGradient(primary → tertiary)` when on — matching the
 * 2dp accent bar in GameGridCard. Thumb is a white circle that slides and
 * scales via [animateFloatAsState].
 */
@Composable
fun ArcadeToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    hapticFeedbackEnabled: Boolean = true
) {
    val haptic = LocalHapticFeedback.current
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary

    // Track dimensions
    val trackWidth = 44.dp
    val trackHeight = 24.dp
    val thumbSize = 18.dp
    val thumbPadding = 3.dp

    // Thumb x-offset: 0 = left (off), 1 = right (on)
    val thumbOffset by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = tween(durationMillis = 220),
        label = "toggleThumb"
    )

    // Thumb scale: pops slightly on press
    val thumbScale by animateFloatAsState(
        targetValue = if (checked) 1.05f else 1f,
        animationSpec = tween(180),
        label = "toggleScale"
    )

    val trackColor by animateColorAsState(
        targetValue = if (checked) Color.Transparent
        else MaterialTheme.colorScheme.surfaceContainerHigh,
        animationSpec = tween(220),
        label = "trackColor"
    )

    val trackBorderBrush = if (checked) {
        Brush.horizontalGradient(
            listOf(
                primary.copy(alpha = 0.85f),
                tertiary.copy(alpha = 0.55f)
            )
        )
    } else {
        Brush.linearGradient(
            listOf(
                MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                Color.Transparent
            )
        )
    }

    Box(
        modifier = modifier
            .width(trackWidth)
            .height(trackHeight)
            .clip(CircleShape)
            .background(
                if (checked) Brush.horizontalGradient(
                    listOf(
                        primary.copy(alpha = 0.75f),
                        tertiary.copy(alpha = 0.55f)
                    )
                ) else Brush.linearGradient(listOf(trackColor, trackColor))
            )
            .border(1.dp, trackBorderBrush, CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    if (hapticFeedbackEnabled) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    onCheckedChange(!checked)
                }
            )
    ) {
        val travelDp = trackWidth - thumbSize - thumbPadding * 2

        Box(
            modifier = Modifier
                .padding(start = thumbPadding + travelDp * thumbOffset, top = thumbPadding)
                .size(thumbSize)
                .scale(thumbScale)
                .clip(CircleShape)
                .background(
                    if (checked) Brush.radialGradient(
                        listOf(
                            Color.White,
                            primary.copy(alpha = 0.15f)
                        )
                    ) else Brush.radialGradient(
                        listOf(
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f)
                        )
                    )
                )
        )
    }
}