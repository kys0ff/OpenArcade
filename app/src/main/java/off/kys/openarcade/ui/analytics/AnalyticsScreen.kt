package off.kys.openarcade.ui.analytics

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import off.kys.openarcade.R
import off.kys.openarcade.domain.model.TopGame
import off.kys.openarcade.ui.components.ArcadeCard
import off.kys.openarcade.ui.components.BarChart
import off.kys.openarcade.ui.components.DonutChart
import off.kys.openarcade.ui.components.LoadingScreen
import off.kys.openarcade.ui.components.SectionHeader
import off.kys.openarcade.ui.components.getDynamicColors
import off.kys.openarcade.ui.launcher.components.ArcadeFilterChip
import off.kys.openarcade.util.ColorExtractor
import org.koin.androidx.compose.koinViewModel

class AnalyticsScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: AnalyticsViewModel = koinViewModel()
        val uiState by viewModel.uiState.collectAsState()
        val isDark = isSystemInDarkTheme()
        val primary = MaterialTheme.colorScheme.primary
        val tertiary = MaterialTheme.colorScheme.tertiary

        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        title = {
                            Text(
                                text = stringResource(R.string.analytics),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black
                                )
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { navigator.pop() }) {
                                Icon(
                                    painter = painterResource(R.drawable.round_arrow_back_24),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        primary.copy(alpha = 0.65f),
                                        tertiary.copy(alpha = 0.30f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            if (uiState.isLoading) {
                LoadingScreen(
                    message = stringResource(R.string.game_detail_loading),
                    modifier = Modifier.padding(innerPadding)
                )
            } else {
                uiState.data?.let { data ->
                    val chartData = remember(uiState.selectedInterval, data) {
                        if (uiState.selectedInterval == AnalyticsInterval.DAILY)
                            data.dailyTrend else data.weeklyTrend
                    }
                    val barColor = remember(primary, isDark) {
                        ColorExtractor.getAdaptiveColor(primary, isDark)
                    }
                    val donutColors = getDynamicColors()
                    val maxPlayTime = remember(data.topGames) {
                        data.topGames.firstOrNull()?.playTimeMs ?: 1L
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {

                        item {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    SectionHeader(
                                        title = stringResource(R.string.play_time_trend),
                                        modifier = Modifier.weight(1f)
                                    )

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        ArcadeFilterChip(
                                            label = stringResource(R.string.daily),
                                            selected = uiState.selectedInterval == AnalyticsInterval.DAILY,
                                            onClick = {
                                                viewModel.onEvent(
                                                    AnalyticsUiEvent.IntervalSelected(AnalyticsInterval.DAILY)
                                                )
                                            }
                                        )
                                        ArcadeFilterChip(
                                            label = stringResource(R.string.weekly),
                                            selected = uiState.selectedInterval == AnalyticsInterval.WEEKLY,
                                            onClick = {
                                                viewModel.onEvent(
                                                    AnalyticsUiEvent.IntervalSelected(AnalyticsInterval.WEEKLY)
                                                )
                                            }
                                        )
                                    }
                                }

                                Spacer(Modifier.height(16.dp))

                                ArcadeCard(modifier = Modifier.fillMaxWidth()) {
                                    Box(modifier = Modifier.padding(16.dp)) {
                                        BarChart(
                                            data = chartData,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(220.dp)
                                                .padding(bottom = 8.dp),
                                            barColor = barColor
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Column {
                                SectionHeader(title = stringResource(R.string.category_distribution))
                                Spacer(Modifier.height(16.dp))
                                ArcadeCard(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        DonutChart(
                                            data = data.categoryDistribution,
                                            modifier = Modifier.size(140.dp),
                                            colors = donutColors
                                        )
                                        Spacer(Modifier.width(24.dp))
                                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                            data.categoryDistribution.take(4)
                                                .forEachIndexed { index, dist ->
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Surface(
                                                            modifier = Modifier.size(8.dp),
                                                            shape = CircleShape,
                                                            color = donutColors[index % donutColors.size]
                                                        ) {}
                                                        Spacer(Modifier.width(8.dp))
                                                        Text(
                                                            text = stringResource(dist.category.displayNameRes),
                                                            style = MaterialTheme.typography.bodySmall.copy(
                                                                fontWeight = FontWeight.SemiBold
                                                            ),
                                                            color = MaterialTheme.colorScheme.onSurface,
                                                            modifier = Modifier.weight(1f),
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                        Spacer(Modifier.width(8.dp))
                                                        Text(
                                                            text = "${(dist.percentage * 100).toInt()}%",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = donutColors[index % donutColors.size]
                                                        )
                                                    }
                                                }
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            SectionHeader(title = stringResource(R.string.top_games))
                        }

                        items(
                            items = data.topGames,
                            key = { it.title }
                        ) { game ->
                            TopGameItem(
                                game = game,
                                maxPlayTime = maxPlayTime,
                                isDark = isDark,
                                formatPlayTime = ::formatPlayTime
                            )
                        }
                    }
                }
            }
        }
    }

    private fun formatPlayTime(ms: Long): String {
        val hours = ms / (1000 * 60 * 60)
        val minutes = (ms / (1000 * 60)) % 60
        return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
    }
}

@Composable
private fun TopGameItem(
    game: TopGame,
    maxPlayTime: Long,
    isDark: Boolean,
    formatPlayTime: (Long) -> String
) {
    val gameColor = remember(game, isDark) {
        ColorExtractor.getAdaptiveColor(Color(game.primaryColorArgb), isDark)
    }
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    val progressTarget = remember(game.playTimeMs, maxPlayTime) {
        game.playTimeMs.toFloat() / maxPlayTime
    }
    val progressAnim by animateFloatAsState(
        targetValue = progressTarget,
        animationSpec = tween(durationMillis = 900),
        label = "topGameProgress"
    )
    val formattedTime = remember(game.playTimeMs) { formatPlayTime(game.playTimeMs) }

    ArcadeCard(
        modifier = Modifier.fillMaxWidth(),
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