package off.kys.openarcade.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ArcadeCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: Shape = MaterialTheme.shapes.large,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    tertiaryColor: Color = MaterialTheme.colorScheme.tertiary,
    borderWidth: Dp = 1.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val containerColor = if (isPressed && onClick != null) {
        accentColor.copy(alpha = 0.14f)
    } else {
        MaterialTheme.colorScheme.surfaceContainerLow
    }

    val animatedContainer by animateColorAsState(
        targetValue = containerColor,
        animationSpec = tween(200),
        label = "arcadeCardContainer"
    )

    val borderBrush = Brush.linearGradient(
        listOf(
            accentColor.copy(alpha = if (isPressed && onClick != null) 0.85f else 0.45f),
            if (isPressed && onClick != null) tertiaryColor.copy(alpha = 0.40f) else Color.Transparent,
            Color.Transparent
        )
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(animatedContainer)
            .border(borderWidth, borderBrush, shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            )
    ) {
        Column {
            // 2dp top accent bar (Token #3)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                accentColor.copy(alpha = 0.70f),
                                tertiaryColor.copy(alpha = 0.35f),
                                Color.Transparent
                            )
                        )
                    )
            )
            content()
        }
    }
}
