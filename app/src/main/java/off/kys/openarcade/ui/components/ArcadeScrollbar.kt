package off.kys.openarcade.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val ScrollbarWidth = 3.dp
val ScrollbarPad = 4.dp

/**
 * Clean interface to abstract layout metrics for uniform scrollbar rendering.
 */
interface ScrollbarAdapter {
    val isScrollInProgress: Boolean
    fun getMetrics(viewportHeight: Float): ScrollbarMetrics?
}

data class ScrollbarMetrics(
    val thumbHeightFraction: Float,
    val scrollFraction: Float
)

@Composable
fun ArcadeScrollbar(
    listState: LazyListState,
    modifier: Modifier = Modifier,
    width: Dp = ScrollbarWidth,
) {
    val adapter = rememberScrollbarAdapter(listState)
    ArcadeScrollbarImpl(adapter, modifier, width)
}

@Composable
fun ArcadeGridScrollbar(
    gridState: LazyGridState,
    modifier: Modifier = Modifier,
    width: Dp = ScrollbarWidth,
) {
    val adapter = rememberScrollbarAdapter(gridState)
    ArcadeScrollbarImpl(adapter, modifier, width)
}

@Composable
fun ArcadeScrollStateScrollbar(
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    width: Dp = ScrollbarWidth,
) {
    val adapter = rememberScrollbarAdapter(scrollState)
    ArcadeScrollbarImpl(adapter, modifier, width)
}

// --- Adapters ---

@Composable
private fun rememberScrollbarAdapter(state: LazyListState): ScrollbarAdapter = remember(state) {
    object : ScrollbarAdapter {
        override val isScrollInProgress: Boolean get() = state.isScrollInProgress
        override fun getMetrics(viewportHeight: Float): ScrollbarMetrics? {
            val layoutInfo = state.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val visibleItems = layoutInfo.visibleItemsInfo
            val firstVisible = visibleItems.firstOrNull() ?: return null
            if (totalItems == 0) return null

            val itemSize = firstVisible.size.toFloat()
            val scrolledOutOffset = -firstVisible.offset.toFloat()
            val exactIndex = firstVisible.index + (scrolledOutOffset / itemSize.coerceAtLeast(1f))
            val maxScrollableItems = (totalItems - visibleItems.size).coerceAtLeast(1)

            return ScrollbarMetrics(
                thumbHeightFraction = visibleItems.size.toFloat() / totalItems,
                scrollFraction = (exactIndex / maxScrollableItems.toFloat()).coerceIn(0f, 1f)
            )
        }
    }
}

@Composable
private fun rememberScrollbarAdapter(state: LazyGridState): ScrollbarAdapter = remember(state) {
    object : ScrollbarAdapter {
        override val isScrollInProgress: Boolean get() = state.isScrollInProgress
        override fun getMetrics(viewportHeight: Float): ScrollbarMetrics? {
            val layoutInfo = state.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val visibleItems = layoutInfo.visibleItemsInfo
            val firstVisible = visibleItems.firstOrNull() ?: return null
            if (totalItems == 0) return null

            val itemSize = firstVisible.size.height.toFloat()
            val scrolledOutOffset = -firstVisible.offset.y.toFloat()
            val exactIndex = firstVisible.index + (scrolledOutOffset / itemSize.coerceAtLeast(1f))
            val maxScrollableItems = (totalItems - visibleItems.size).coerceAtLeast(1)

            return ScrollbarMetrics(
                thumbHeightFraction = visibleItems.size.toFloat() / totalItems,
                scrollFraction = (exactIndex / maxScrollableItems.toFloat()).coerceIn(0f, 1f)
            )
        }
    }
}

@Composable
private fun rememberScrollbarAdapter(state: ScrollState): ScrollbarAdapter = remember(state) {
    object : ScrollbarAdapter {
        override val isScrollInProgress: Boolean get() = state.isScrollInProgress
        override fun getMetrics(viewportHeight: Float): ScrollbarMetrics? {
            val maxVal = state.maxValue
            if (maxVal <= 0) return null
            val totalHeight = maxVal + viewportHeight
            return ScrollbarMetrics(
                thumbHeightFraction = viewportHeight / totalHeight,
                scrollFraction = state.value.toFloat() / maxVal
            )
        }
    }
}

// --- Shared Core Implementation ---

@Composable
private fun ArcadeScrollbarImpl(
    adapter: ScrollbarAdapter,
    modifier: Modifier = Modifier,
    width: Dp = ScrollbarWidth,
) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary

    val isScrolling = adapter.isScrollInProgress
    val thumbAlpha by animateFloatAsState(
        targetValue = if (isScrolling) 0.85f else 0.30f,
        animationSpec = tween(if (isScrolling) 100 else 600),
        label = "scrollbarAlpha"
    )

    Box(
        modifier = modifier.drawWithContent {
            drawContent()

            val viewportH = size.height
            val metrics = adapter.getMetrics(viewportH) ?: return@drawWithContent

            val minThumbPx = 40.dp.toPx()
            val thumbH = (viewportH * metrics.thumbHeightFraction).coerceAtLeast(minThumbPx)
            val thumbY = metrics.scrollFraction * (viewportH - thumbH)
            val widthPx = width.toPx()

            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(
                        primary.copy(alpha = thumbAlpha),
                        tertiary.copy(alpha = thumbAlpha * 0.60f)
                    )
                ),
                topLeft = Offset(size.width - widthPx, thumbY),
                size = Size(widthPx, thumbH),
                cornerRadius = CornerRadius(widthPx / 2f)
            )
        }
    )
}