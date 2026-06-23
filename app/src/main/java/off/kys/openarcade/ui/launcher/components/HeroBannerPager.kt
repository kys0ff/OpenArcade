package off.kys.openarcade.ui.launcher.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import off.kys.openarcade.R
import off.kys.openarcade.domain.model.GameEntry
import off.kys.openarcade.util.ColorExtractor
import kotlin.math.absoluteValue

@Composable
fun HeroBannerPager(
    modifier: Modifier = Modifier,
    installedGames: List<GameEntry>,
    onInspectGame: (String) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val pagerCount = minOf(installedGames.size, 5)
    val pagerState = rememberPagerState { pagerCount }

    Box(
        modifier = modifier.layout { measurable, constraints ->
            val bleed = 16.dp.roundToPx()
            val placeable = measurable.measure(
                constraints.copy(maxWidth = (constraints.maxWidth + (bleed * 2)))
            )
            layout(constraints.maxWidth, placeable.height) {
                placeable.placeRelative(-bleed, 0)
            }
        }
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) { page ->
            val game = installedGames[page]
            val context = LocalContext.current
            val iconModel = remember(game.packageName, game.customIconPath) {
                game.customIconPath
                    ?: try {
                        context.packageManager.getApplicationIcon(game.packageName)
                    } catch (_: Exception) {
                        null
                    }
            }
            val pageOffset by remember(pagerState.currentPage) {
                derivedStateOf { (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(game.getPrimaryColor(alpha = 1.0f))
                )

                AsyncImage(
                    model = iconModel,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            translationX = pageOffset * 72.dp.toPx()
                            scaleX = 1f + (pageOffset.absoluteValue * 0.06f).coerceAtMost(0.12f)
                            scaleY = 1f + (pageOffset.absoluteValue * 0.06f).coerceAtMost(0.12f)
                        },
                    contentScale = ContentScale.Crop,
                    alpha = 0.32f
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.background.copy(alpha = 0.45f),
                                    Color.Transparent
                                )
                            )
                        )
                        .height(48.dp)
                        .statusBarsPadding()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colorStops = arrayOf(
                                    0.0f to Color.Transparent,
                                    0.45f to Color.Black.copy(alpha = 0.15f),
                                    1.0f to Color.Black.copy(alpha = 0.88f)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 24.dp, end = 88.dp, bottom = 44.dp)
                ) {
                    Text(
                        text = game.displayName,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(3.dp))
                    val categoryStrings =
                        game.customCategories + if (game.category.displayNameRes != 0) {
                            listOf(stringResource(game.category.displayNameRes))
                        } else emptyList()

                    Text(
                        text = categoryStrings.joinToString(" · "),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.65f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                FloatingActionButton(
                    onClick = { onInspectGame(game.packageName) },
                    containerColor = remember(game.primaryColorArgb, isDark) {
                        ColorExtractor.getAdaptiveColor(game.getPrimaryColor(), isDark)
                    },
                    contentColor = game.getOnPrimaryColor(),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 20.dp, bottom = 20.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.round_play_arrow_24),
                        contentDescription = stringResource(R.string.play_game, game.displayName)
                    )
                }
            }
        }

        if (pagerCount > 1) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 24.dp, bottom = 24.dp)
            ) {
                repeat(pagerCount) { index ->
                    val isActive = pagerState.currentPage == index
                    val dotWidth by animateDpAsState(
                        targetValue = if (isActive) 20.dp else 6.dp,
                        animationSpec = tween(250),
                        label = "dotWidth"
                    )
                    val activeGameColor = remember(pagerState.currentPage, isDark) {
                        val color =
                            installedGames.getOrNull(pagerState.currentPage)?.getPrimaryColor()
                                ?: Color.White
                        ColorExtractor.getAdaptiveColor(color, isDark)
                    }

                    Box(
                        modifier = Modifier
                            .size(width = dotWidth, height = 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (isActive) activeGameColor
                                else Color.White.copy(alpha = 0.30f)
                            )
                    )
                }
            }
        }
    }
}
