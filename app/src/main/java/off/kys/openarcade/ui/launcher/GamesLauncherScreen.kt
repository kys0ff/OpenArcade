package off.kys.openarcade.ui.launcher

import android.content.Intent
import android.provider.Settings
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import off.kys.openarcade.R
import off.kys.openarcade.domain.model.GameFilter
import off.kys.openarcade.ui.components.LoadingScreen
import off.kys.openarcade.ui.detail.GameDetailScreen
import off.kys.openarcade.ui.launcher.components.AnalyticsSection
import off.kys.openarcade.ui.launcher.components.GameGridCard
import off.kys.openarcade.ui.launcher.components.HeroBannerPager
import off.kys.openarcade.ui.launcher.components.RecentActivitySection
import off.kys.openarcade.ui.launcher.components.SystemStatusSection
import off.kys.openarcade.ui.launcher.components.UsagePermissionCard
import org.koin.androidx.compose.koinViewModel

class GamesLauncherScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: GamesLauncherViewModel = koinViewModel()
        val uiState by viewModel.uiState.collectAsState()

        val context = LocalContext.current

        LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
            viewModel.onEvent(GamesLauncherUiEvent.PermissionCheckRequested)
        }

        if (uiState.isLoading && uiState.filteredGames.isEmpty()) {
            LoadingScreen(message = stringResource(R.string.searching_for_games))
        } else {
            Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            top = 0.dp,
                            start = 16.dp,
                            end = 16.dp,
                            bottom = innerPadding.calculateBottomPadding() + 16.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        if (uiState.filteredGames.isNotEmpty()) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                HeroBannerPager(
                                    installedGames = uiState.filteredGames,
                                    onInspectGame = { pkg ->
                                        viewModel.onEvent(GamesLauncherUiEvent.GameClicked(pkg))
                                        navigator.push(GameDetailScreen(pkg))
                                    },
                                    modifier = Modifier.layout { measurable, constraints ->
                                        val bleed = 16.dp.roundToPx()
                                        val placeable = measurable.measure(
                                            constraints.copy(maxWidth = (constraints.maxWidth + (bleed * 2)))
                                        )
                                        layout(constraints.maxWidth, placeable.height) {
                                            placeable.placeRelative(-bleed, 0)
                                        }
                                    }
                                )
                            }
                        } else {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .statusBarsPadding()
                                        .padding(top = 80.dp, bottom = 32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.round_sports_esports_24),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                alpha = 0.4f
                                            ),
                                            modifier = Modifier.size(56.dp)
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = stringResource(R.string.no_games_here),
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = stringResource(R.string.no_games_subtitle),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        item(span = { GridItemSpan(maxLineSpan) }) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(vertical = 8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(
                                    count = uiState.filters.size,
                                    key = { index ->
                                        when (val filter = uiState.filters[index]) {
                                            is GameFilter.All -> "all"
                                            is GameFilter.Installed -> "installed"
                                            is GameFilter.Uninstalled -> "uninstalled"
                                            is GameFilter.System -> "system_${filter.category.name}"
                                            is GameFilter.Custom -> "custom_${filter.name}"
                                        }
                                    }
                                ) { index ->
                                    val filter = uiState.filters[index]
                                    val isSelected = uiState.selectedFilter == filter

                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            viewModel.onEvent(
                                                GamesLauncherUiEvent.FilterSelected(
                                                    filter
                                                )
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = when (filter) {
                                                    is GameFilter.All -> stringResource(R.string.filter_all)
                                                    is GameFilter.Installed -> stringResource(R.string.category_installed)
                                                    is GameFilter.Uninstalled -> stringResource(R.string.category_uninstalled)
                                                    is GameFilter.System -> stringResource(filter.category.displayNameRes)
                                                    is GameFilter.Custom -> filter.name
                                                },
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                                )
                                            )
                                        },
                                        leadingIcon = if (isSelected) {
                                            {
                                                Icon(
                                                    painter = painterResource(R.drawable.round_check_24),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        } else null,
                                        shape = CircleShape,
                                        colors = FilterChipDefaults.filterChipColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                                alpha = 0.3f
                                            ),
                                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = isSelected,
                                            borderColor = Color.Transparent,
                                            selectedBorderColor = Color.Transparent,
                                            disabledBorderColor = Color.Transparent,
                                            disabledSelectedBorderColor = Color.Transparent
                                        )
                                    )
                                }
                            }
                        }

                        if (!uiState.hasUsageStatsPermission) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                UsagePermissionCard {
                                    viewModel.onEvent(GamesLauncherUiEvent.GrantPermissionClicked)
                                    context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                                }
                            }
                        }

                        item(span = { GridItemSpan(maxLineSpan) }) {
                            SystemStatusSection(
                                batteryLevel = uiState.batteryLevel,
                                storageUsage = uiState.storageUsage
                            )
                        }

                        item(span = { GridItemSpan(maxLineSpan) }) {
                            AnalyticsSection(uiState.filteredGames)
                        }

                        if (uiState.recentGames.isNotEmpty()) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                RecentActivitySection(
                                    games = uiState.recentGames,
                                    onGameClick = { pkg ->
                                        viewModel.onEvent(GamesLauncherUiEvent.GameClicked(pkg))
                                        navigator.push(GameDetailScreen(pkg))
                                    }
                                )
                            }
                        }

                        if (uiState.filteredGames.isNotEmpty()) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = stringResource(R.string.your_library),
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = stringResource(R.string.games_count, uiState.filteredGames.size),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        items(uiState.filteredGames, key = { it.packageName }) { game ->
                            GameGridCard(
                                game = game,
                                onClick = {
                                    viewModel.onEvent(
                                        GamesLauncherUiEvent.GameClicked(
                                            game.packageName
                                        )
                                    )
                                    navigator.push(GameDetailScreen(game.packageName))
                                }
                            )
                        }
                    }

                    if (uiState.isLoading) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .align(Alignment.TopCenter),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = Color.Transparent
                        )
                    }
                }
            }
        }
    }
}
