package off.kys.openarcade.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.nativePaint
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import off.kys.openarcade.domain.model.PlayTimePoint
import off.kys.openarcade.util.ColorExtractor

@Composable
fun BarChart(
    data: List<PlayTimePoint>,
    modifier: Modifier = Modifier,
    barColor: Color = ColorExtractor.getAdaptiveColor(
        MaterialTheme.colorScheme.primary,
        isSystemInDarkTheme()
    ),
    labelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    val isDark = isSystemInDarkTheme()
    val barGradientEnd = ColorExtractor.getAdaptiveColor(
        MaterialTheme.colorScheme.tertiary, isDark
    )
    val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)

    val maxPlayTime = data.maxOfOrNull { it.playTimeMs }?.coerceAtLeast(1L) ?: 1L

    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(data) {
        animProgress.snapTo(0f)
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
        )
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 8.dp)
    ) {
        val w = size.width
        val h = size.height
        val barW = (w / data.size) * 0.55f
        val gap = (w / data.size) * 0.45f
        val radius = CornerRadius(barW / 2, barW / 2)

        repeat(3) { i ->
            val y = h * (1f - (i + 1) / 4f)
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        data.forEachIndexed { index, point ->
            val fraction = (point.playTimeMs.toFloat() / maxPlayTime) * animProgress.value
            val barH = fraction * h
            val x = index * (barW + gap) + (gap / 2)
            val y = h - barH

            drawIntoCanvas { canvas ->
                val glowPaint = Paint().nativePaint.apply {
                    isAntiAlias = true
                    color = android.graphics.Color.TRANSPARENT
                    setShadowLayer(
                        barW * 0.6f,
                        0f, 4.dp.toPx(),
                        barColor.copy(alpha = 0.35f).toArgb()
                    )
                }
                canvas.nativeCanvas.drawRoundRect(
                    x, y, x + barW, h,
                    barW / 2, barW / 2,
                    glowPaint
                )
            }

            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        barColor.copy(alpha = 0.95f),
                        barGradientEnd.copy(alpha = 0.55f)
                    ),
                    startY = y,
                    endY = h
                ),
                topLeft = Offset(x, y),
                size = Size(barW, barH),
                cornerRadius = radius
            )

            if (barH > 8.dp.toPx()) {
                drawRoundRect(
                    color = barColor.copy(alpha = 0.90f),
                    topLeft = Offset(x, y),
                    size = Size(barW, 4.dp.toPx()),
                    cornerRadius = radius
                )
            }

            drawIntoCanvas { canvas ->
                val paint = android.graphics.Paint().apply {
                    color = labelColor.toArgb()
                    textSize = 10.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                    isAntiAlias = true
                }
                canvas.nativeCanvas.drawText(
                    point.label,
                    x + barW / 2,
                    h + 18.dp.toPx(),
                    paint
                )
            }
        }
    }
}