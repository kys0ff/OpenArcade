package off.kys.openarcade.ui.analytics

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            stringResource(R.string.analytics),
                            fontWeight = FontWeight.Black
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                painter = painterResource(R.drawable.round_arrow_back_24),
                                contentDescription = null
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
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
                    // 1. Remember trend data filtering so it doesn't re-calculate every frame
                    val chartData = remember(uiState.selectedInterval, data) {
                        if (uiState.selectedInterval == AnalyticsInterval.DAILY) {
                            data.dailyTrend
                        } else {
                            data.weeklyTrend
                        }
                    }

                    // 2. Remember adaptive bar color
                    val primaryColor = MaterialTheme.colorScheme.primary
                    val barColor = remember(primaryColor, isDark) {
                        ColorExtractor.getAdaptiveColor(primaryColor, isDark)
                    }

                    // 3. Remember donut color allocation and map operation
                    val donutColors = getDynamicColors()

                    // 4. Precompute the max play time to avoid calculating it for every row item
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
                        // Play Time Trend
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
                                    SingleChoiceSegmentedButtonRow {
                                        SegmentedButton(
                                            selected = uiState.selectedInterval == AnalyticsInterval.DAILY,
                                            onClick = {
                                                viewModel.onEvent(
                                                    AnalyticsUiEvent.IntervalSelected(
                                                        AnalyticsInterval.DAILY
                                                    )
                                                )
                                            },
                                            shape = SegmentedButtonDefaults.itemShape(
                                                index = 0,
                                                count = 2
                                            )
                                        ) {
                                            Text(stringResource(R.string.daily))
                                        }
                                        SegmentedButton(
                                            selected = uiState.selectedInterval == AnalyticsInterval.WEEKLY,
                                            onClick = {
                                                viewModel.onEvent(
                                                    AnalyticsUiEvent.IntervalSelected(
                                                        AnalyticsInterval.WEEKLY
                                                    )
                                                )
                                            },
                                            shape = SegmentedButtonDefaults.itemShape(
                                                index = 1,
                                                count = 2
                                            )
                                        ) {
                                            Text(stringResource(R.string.weekly))
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                ArcadeCard(modifier = Modifier.fillMaxWidth()) {
                                    Box(modifier = Modifier.padding(16.dp)) {
                                        BarChart(
                                            data = chartData,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(240.dp),
                                            barColor = barColor
                                        )
                                    }
                                }
                            }
                        }

                        // Category Distribution
                        item {
                            Column {
                                SectionHeader(title = stringResource(R.string.category_distribution))
                                Spacer(modifier = Modifier.height(16.dp))
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
                                        Spacer(modifier = Modifier.width(24.dp))
                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            data.categoryDistribution.take(4)
                                                .forEachIndexed { index, dist ->
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Surface(
                                                            modifier = Modifier.size(8.dp),
                                                            shape = MaterialTheme.shapes.small,
                                                            color = donutColors[index % donutColors.size]
                                                        ) {}
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(
                                                            text = dist.category.name,
                                                            style = MaterialTheme.typography.bodySmall,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Text(
                                                            text = "${(dist.percentage * 100).toInt()}%",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }
                                        }
                                    }
                                }
                            }
                        }

                        // Top Games Header
                        item {
                            SectionHeader(title = stringResource(R.string.top_games))
                        }

                        // 5. Extracted into its own item block with stable sub-composition keys
                        items(
                            items = data.topGames,
                            key = { it.title } // Use a unique identifier if game has an ID
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

    val progressValue = remember(game.playTimeMs, maxPlayTime) {
        game.playTimeMs.toFloat() / maxPlayTime
    }

    val formattedTime = remember(game.playTimeMs) {
        formatPlayTime(game.playTimeMs)
    }

    ArcadeCard(
        modifier = Modifier.fillMaxWidth(),
        accentColor = gameColor
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = game.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            LinearProgressIndicator(
                progress = { progressValue },
                modifier = Modifier.width(64.dp),
                color = gameColor,
                trackColor = gameColor.copy(alpha = 0.2f),
                strokeCap = StrokeCap.Round
            )
        }
    }
}