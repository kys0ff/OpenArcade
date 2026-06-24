package off.kys.openarcade.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import off.kys.openarcade.R

enum class GameActionItemRole { Default, Destructive }

@Composable
fun GameActionItem(
    text: String,
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    role: GameActionItemRole = GameActionItemRole.Default,
    accentColorOverride: Color? = null
) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val error = MaterialTheme.colorScheme.error

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val baseAccent = accentColorOverride ?: when (role) {
        GameActionItemRole.Default -> primary
        GameActionItemRole.Destructive -> error
    }

    val containerColor = baseAccent.copy(alpha = if (isPressed) 0.14f else 0.0f)
    val animatedContainer by animateColorAsState(
        targetValue = containerColor,
        animationSpec = tween(200),
        label = "actionItemContainer"
    )

    val borderBrush = Brush.linearGradient(
        listOf(
            baseAccent.copy(alpha = if (isPressed) 0.55f else 0.18f),
            tertiary.copy(alpha = if (isPressed) 0.30f else 0.0f),
            Color.Transparent
        )
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(animatedContainer, MaterialTheme.shapes.medium)
            .border(1.dp, borderBrush, MaterialTheme.shapes.medium)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Icon Badge (Token #5)
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(MaterialTheme.shapes.small)
                .background(
                    Brush.radialGradient(
                        listOf(
                            baseAccent.copy(alpha = 0.18f),
                            baseAccent.copy(alpha = 0.04f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            baseAccent.copy(alpha = 0.45f),
                            Color.Transparent
                        )
                    ),
                    shape = MaterialTheme.shapes.small
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = baseAccent,
                modifier = Modifier.size(20.dp)
            )
        }

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        Icon(
            painter = painterResource(R.drawable.round_chevron_left_24),
            contentDescription = null,
            tint = baseAccent.copy(alpha = 0.35f),
            modifier = Modifier
                .size(14.dp)
                .graphicsLayer { rotationZ = 180f }
        )
    }
}
