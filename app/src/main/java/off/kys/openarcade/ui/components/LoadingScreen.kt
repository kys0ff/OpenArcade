package off.kys.openarcade.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import off.kys.openarcade.ui.theme.OpenArcadeTheme

private val DotSize = 10.dp
private val DotSpacing = 10.dp
private const val DotCount = 4
private val DotBounceHeight = 10.dp
private const val StaggerMs = 120
private const val CycleDurationMs = 900
private val ScanBarHeight = 2.dp
private val ScanBarWidth = 120.dp

/**
 * A row of [DotCount] dots that bounce in a staggered wave, each tinted
 * along the primary→tertiary gradient — matching the card border language.
 */
@Composable
fun ArcadeLoadingIndicator(
    modifier: Modifier = Modifier,
    dotSize: Dp = DotSize,
    dotSpacing: Dp = DotSpacing
) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary

    // Evenly interpolate a color for each dot across primary→tertiary
    val dotColors = List(DotCount) { i ->
        lerp(primary, tertiary, i / (DotCount - 1).toFloat())
    }

    val transition = rememberInfiniteTransition(label = "arcadeDots")

    // One animated float per dot, staggered by StaggerMs
    val offsets = List(DotCount) { i ->
        val delay = i * StaggerMs
        val bounce by transition.animateFloat(
            initialValue = 0f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = CycleDurationMs + (DotCount - 1) * StaggerMs
                    0f at delay using FastOutSlowInEasing
                    (-1f) at delay + CycleDurationMs / 2 using FastOutSlowInEasing
                    0f at delay + CycleDurationMs using FastOutSlowInEasing
                },
                repeatMode = RepeatMode.Restart
            ),
            label = "dotBounce_$i"
        )
        bounce
    }

    val scales = List(DotCount) { i ->
        val delay = i * StaggerMs
        val scale by transition.animateFloat(
            initialValue = 1f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = CycleDurationMs + (DotCount - 1) * StaggerMs
                    1f at delay using FastOutSlowInEasing
                    1.35f at delay + CycleDurationMs / 2 using FastOutSlowInEasing
                    1f at delay + CycleDurationMs using FastOutSlowInEasing
                },
                repeatMode = RepeatMode.Restart
            ),
            label = "dotScale_$i"
        )
        scale
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(dotSpacing),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        repeat(DotCount) { i ->
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .offset(y = (offsets[i] * DotBounceHeight.value).dp)
                    .scale(scales[i])
                    .clip(CircleShape)
                    .background(dotColors[i])
            )
        }
    }
}

/**
 * A horizontal gradient bar that fades in and out — references the
 * 2dp accent divider in GameGridCard.
 */
@Composable
fun ArcadeScanBar(
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary

    val transition = rememberInfiniteTransition(label = "scanBar")
    val alpha by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanAlpha"
    )

    Box(
        modifier = modifier
            .width(ScanBarWidth)
            .height(ScanBarHeight)
            .alpha(alpha)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        primary.copy(alpha = 0.90f),
                        tertiary.copy(alpha = 0.45f),
                        Color.Transparent
                    )
                )
            )
    )
}

@Composable
fun LoadingScreen(
    message: String,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary

    // Radial wash transitioning from primary to tertiary with subtle alphas
    val backgroundBrush = Brush.radialGradient(
        listOf(
            primary.copy(alpha = 0.07f),
            tertiary.copy(alpha = 0.02f),
            Color.Transparent
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(280.dp)
                .background(backgroundBrush)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            ArcadeLoadingIndicator()

            Spacer(Modifier.height(16.dp))

            ArcadeScanBar()

            Spacer(Modifier.height(20.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Linear interpolation between two [Color]s by [fraction] ∈ [0, 1]. */
private fun lerp(start: Color, stop: Color, fraction: Float): Color = Color(
    red   = start.red   + (stop.red   - start.red)   * fraction,
    green = start.green + (stop.green - start.green) * fraction,
    blue  = start.blue  + (stop.blue  - start.blue)  * fraction,
    alpha = start.alpha + (stop.alpha - start.alpha) * fraction
)

@Preview(showBackground = true)
@Composable
fun LoadingScreenPreview() {
    OpenArcadeTheme {
        LoadingScreen(message = "Loading games…")
    }
}