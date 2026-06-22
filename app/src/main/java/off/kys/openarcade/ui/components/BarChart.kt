package off.kys.openarcade.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import off.kys.openarcade.domain.model.PlayTimePoint
import off.kys.openarcade.util.ColorExtractor

@Composable
fun BarChart(
    data: List<PlayTimePoint>,
    modifier: Modifier = Modifier,
    barColor: Color = ColorExtractor.getAdaptiveColor(MaterialTheme.colorScheme.primary, isSystemInDarkTheme()),
    labelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    val maxPlayTime = data.maxOfOrNull { it.playTimeMs }?.coerceAtLeast(1L) ?: 1L
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animatedProgress.snapTo(0f)
        animatedProgress.animateTo(1f, TweenSpec(durationMillis = 800))
    }

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 8.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val barWidth = (width / data.size) * 0.6f
                val spacing = (width / data.size) * 0.4f
                val cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)

                data.forEachIndexed { index, point ->
                    val barHeight = (point.playTimeMs.toFloat() / maxPlayTime) * height * animatedProgress.value
                    val x = index * (barWidth + spacing) + (spacing / 2)
                    val y = height - barHeight

                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                barColor,
                                barColor.copy(alpha = 0.3f)
                            )
                        ),
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight),
                        cornerRadius = cornerRadius
                    )

                    // Draw labels using native canvas for simplicity with positioning
                    drawIntoCanvas { canvas ->
                        val paint = android.graphics.Paint().apply {
                            color = labelColor.toArgb()
                            textSize = 10.sp.toPx()
                            textAlign = android.graphics.Paint.Align.CENTER
                            typeface = android.graphics.Typeface.DEFAULT_BOLD
                        }
                        canvas.nativeCanvas.drawText(
                            point.label,
                            x + barWidth / 2,
                            height + 20.dp.toPx(),
                            paint
                        )
                    }
                }
            }
        }
    }
}
