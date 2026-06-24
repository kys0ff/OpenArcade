package off.kys.openarcade.ui.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.skydoves.cloudy.cloudy
import off.kys.openarcade.R
import off.kys.openarcade.domain.model.GameEntry

@Composable
fun GameDetailHeader(
    currentGame: GameEntry,
    dominantColor: Color,
    tertiaryColor: Color,
    haloAlpha: Float,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val iconModel = remember(currentGame.packageName, currentGame.customIconPath) {
        currentGame.customIconPath
            ?: try {
                context.packageManager.getApplicationIcon(currentGame.packageName)
            } catch (_: Exception) {
                null
            }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(172.dp)
                    .cloudy(radius = 40)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                dominantColor.copy(alpha = haloAlpha),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )

            OutlinedCard(
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        listOf(
                            tertiaryColor.copy(alpha = 0.55f),
                            dominantColor.copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    ),
                    width = 1.dp
                ),
                modifier = Modifier.size(136.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = iconModel,
                        contentDescription = stringResource(
                            R.string.game_detail_icon_desc,
                            currentGame.displayName
                        ),
                        modifier = Modifier.fillMaxSize(0.65f)
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = currentGame.displayName,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = currentGame.packageName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 40.dp)
        )
    }
}