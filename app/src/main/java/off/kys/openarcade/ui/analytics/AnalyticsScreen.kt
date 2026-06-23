package off.kys.openarcade.ui.analytics

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import off.kys.openarcade.R
import off.kys.openarcade.ui.analytics.components.CategoryDistributionSection
import off.kys.openarcade.ui.analytics.components.PlayTimeTrendSection
import off.kys.openarcade.ui.analytics.components.TopGameItem
import off.kys.openarcade.ui.components.AnalyticsTopBar
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
        val colorScheme = MaterialTheme.colorScheme

        Scaffold(
            topBar = {
                AnalyticsTopBar(
                    onBackClick = { navigator.pop() },
                    primaryColor = colorScheme.primary,
                    tertiaryColor = colorScheme.tertiary
                )
            },
        ) { innerPadding ->
            if (uiState.isLoading) {
                LoadingScreen(
                    message = stringResource(R.string.game_detail_loading),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            } else {
                val data = uiState.data
                if (data != null) {
                    val chartData = remember(uiState.selectedInterval, data) {
                        if (uiState.selectedInterval == AnalyticsInterval.DAILY) {
                            data.dailyTrend
                        } else {
                            data.weeklyTrend
                        }
                    }
                    val barColor = remember(colorScheme.primary, isDark) {
                        ColorExtractor.getAdaptiveColor(colorScheme.primary, isDark)
                    }
                    val donutColors = getDynamicColors()
                    val maxPlayTime =
                        remember(data.topGames) { data.topGames.firstOrNull()?.playTimeMs ?: 1L }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = innerPadding.calculateTopPadding() + 16.dp,
                            bottom = innerPadding.calculateBottomPadding() + 16.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        item {
                            PlayTimeTrendSection(
                                selectedInterval = uiState.selectedInterval,
                                chartData = chartData,
                                barColor = barColor,
                                onIntervalSelected = { interval ->
                                    viewModel.onEvent(AnalyticsUiEvent.IntervalSelected(interval))
                                }
                            )
                        }

                        item {
                            CategoryDistributionSection(
                                data = data.categoryDistribution,
                                donutColors = donutColors
                            )
                        }

                        item {
                            SectionHeader(title = stringResource(R.string.top_games))
                        }

                        items(
                            items = data.topGames,
                            key = { it.packageName }
                        ) { game ->
                            TopGameItem(
                                game = game,
                                maxPlayTime = maxPlayTime,
                                isDark = isDark
                            )
                        }
                    }
                }
            }
        }
    }
}