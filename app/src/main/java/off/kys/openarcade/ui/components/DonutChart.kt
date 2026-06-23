package off.kys.openarcade.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.nativePaint
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import off.kys.openarcade.domain.model.CategoryDistribution

private const val SegmentGapDeg = 3f

@Composable
fun DonutChart(
    data: List<CategoryDistribution>,
    modifier: Modifier = Modifier,
    colors: List<Color> = getDynamicColors()
) {
    val trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.18f)

    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(data) {
        animProgress.snapTo(0f)
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing)
        )
    }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeW = 22.dp.toPx()
            val glowStrokeW = strokeW * 2.2f
            val radius = (size.minDimension - strokeW) / 2
            val center = Offset(size.width / 2, size.height / 2)
            val arcTopLeft = Offset(center.x - radius, center.y - radius)
            val arcSize = Size(radius * 2, radius * 2)

            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeW),
                topLeft = arcTopLeft,
                size = arcSize
            )

            var startAngle = -90f

            data.forEachIndexed { index, dist ->
                val totalSweep = dist.percentage * 360f
                val sweep = ((totalSweep - SegmentGapDeg).coerceAtLeast(0f)) * animProgress.value
                val color = colors[index % colors.size]

                drawIntoCanvas { canvas ->
                    val glowPaint = Paint().nativePaint.apply {
                        isAntiAlias = true
                        style = android.graphics.Paint.Style.STROKE
                        strokeWidth = glowStrokeW
                        strokeCap = android.graphics.Paint.Cap.ROUND
                        this.color = android.graphics.Color.TRANSPARENT
                        setShadowLayer(
                            strokeW * 0.7f,
                            0f, 0f,
                            color.copy(alpha = 0.40f).toArgb()
                        )
                    }
                    canvas.nativeCanvas.drawArc(
                        arcTopLeft.x, arcTopLeft.y,
                        arcTopLeft.x + arcSize.width, arcTopLeft.y + arcSize.height,
                        startAngle, sweep, false, glowPaint
                    )
                }

                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = Stroke(width = strokeW, cap = StrokeCap.Round),
                    topLeft = arcTopLeft,
                    size = arcSize
                )

                startAngle += totalSweep
            }

            val glowR = radius * 0.52f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        colors.firstOrNull()?.copy(alpha = 0.10f) ?: Color.Transparent,
                        Color.Transparent
                    ),
                    center = center,
                    radius = glowR
                ),
                radius = glowR,
                center = center
            )
        }
    }
}