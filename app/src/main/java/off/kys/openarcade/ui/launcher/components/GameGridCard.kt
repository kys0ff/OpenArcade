package off.kys.openarcade.ui.launcher.components

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import off.kys.openarcade.R
import off.kys.openarcade.domain.model.GameEntry
import off.kys.openarcade.util.ColorExtractor

@Composable
fun GameGridCard(
    game: GameEntry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val adaptivePrimary = ColorExtractor.getAdaptiveColor(game.getPrimaryColor(), isDark)
    val adaptiveTertiary = ColorExtractor.getAdaptiveColor(game.getTertiaryColor(), isDark)

    OutlinedCard(
        onClick = onClick,
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(
                    adaptiveTertiary.copy(alpha = 0.45f),
                    Color.Transparent
                )
            )
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.25f)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                adaptivePrimary.copy(alpha = 0.18f),
                                adaptivePrimary.copy(alpha = 0.04f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = MaterialTheme.shapes.small,
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    AsyncImage(
                        model = game.icon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(52.dp)
                            .padding(4.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                adaptivePrimary.copy(alpha = 0.70f),
                                adaptiveTertiary.copy(alpha = 0.35f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
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
                val lastPlayedText = if (game.lastPlayed > 0) {
                    DateUtils.getRelativeTimeSpanString(
                        game.lastPlayed,
                        System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS
                    ).toString()
                } else {
                    stringResource(R.string.never_played)
                }

                Text(
                    text = lastPlayedText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
