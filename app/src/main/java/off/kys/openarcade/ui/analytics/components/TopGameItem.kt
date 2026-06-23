package off.kys.openarcade.ui.analytics.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import off.kys.openarcade.domain.model.TopGame
import off.kys.openarcade.ui.components.ArcadeCard
import off.kys.openarcade.util.ColorExtractor

@Composable
fun TopGameItem(
    game: TopGame,
    maxPlayTime: Long,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val gameColor = remember(game.primaryColorArgb, isDark) {
        ColorExtractor.getAdaptiveColor(Color(game.primaryColorArgb), isDark)
    }
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    val progressTarget = remember(game.playTimeMs, maxPlayTime) {
        if (maxPlayTime > 0) game.playTimeMs.toFloat() / maxPlayTime else 0f
    }
    val progressAnim by animateFloatAsState(
        targetValue = progressTarget,
        animationSpec = tween(durationMillis = 900),
        label = "topGameProgress"
    )
    val formattedTime = remember(game.playTimeMs) { formatPlayTime(game.playTimeMs) }

    ArcadeCard(
        modifier = modifier.fillMaxWidth(),
        accentColor = gameColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = game.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = gameColor
                )
            }

            Spacer(Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .width(72.dp)
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(gameColor.copy(alpha = 0.15f))
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            listOf(
                                gameColor.copy(alpha = 0.30f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(progressAnim)
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    gameColor.copy(alpha = 0.95f),
                                    tertiaryColor.copy(alpha = 0.55f)
                                )
                            )
                        )
                )
            }
        }
    }
}

private fun formatPlayTime(ms: Long): String {
    val hours = ms / (1000 * 60 * 60)
    val minutes = (ms / (1000 * 60)) % 60
    return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
}
