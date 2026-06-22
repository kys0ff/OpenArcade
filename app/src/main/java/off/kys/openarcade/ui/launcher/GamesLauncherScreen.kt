package off.kys.openarcade.ui.launcher

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import off.kys.openarcade.ui.detail.GameDetailScreen
import off.kys.openarcade.ui.launcher.components.AnalyticsSection
import off.kys.openarcade.ui.launcher.components.EmptyGamesState
import off.kys.openarcade.ui.launcher.components.FilterChipsRow
import off.kys.openarcade.ui.launcher.components.GameGridCard
import off.kys.openarcade.ui.launcher.components.HeroBannerPager
import off.kys.openarcade.ui.launcher.components.LibraryHeader
import off.kys.openarcade.ui.launcher.components.RecentActivitySection
import off.kys.openarcade.ui.launcher.components.SystemStatusSection
import off.kys.openarcade.ui.launcher.components.UsagePermissionCard
import off.kys.openarcade.ui.main.MainActivity
import org.koin.androidx.compose.koinViewModel

class GamesLauncherScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        val viewModel: GamesLauncherViewModel = koinViewModel(
            viewModelStoreOwner = context as MainActivity
        )
        val uiState by viewModel.uiState.collectAsState()

        LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
            viewModel.onEvent(GamesLauncherUiEvent.PermissionCheckRequested)
            viewModel.onEvent(GamesLauncherUiEvent.RefreshStats)
        }

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
                                }
                            )
                        }
                    } else {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            EmptyGamesState()
                        }
                    }

                    item(span = { GridItemSpan(maxLineSpan) }) {
                        FilterChipsRow(
                            uiState = uiState,
                            onFilterSelected = { filter ->
                                viewModel.onEvent(GamesLauncherUiEvent.FilterSelected(filter))
                            }
                        )
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
                            LibraryHeader(
                                gameCount = uiState.filteredGames.size,
                                selectedSort = uiState.selectedSort,
                                onSortSelected = { sort ->
                                    viewModel.onEvent(GamesLauncherUiEvent.SortSelected(sort))
                                }
                            )
                        }
                    }

                    items(uiState.filteredGames, key = { it.packageName }) { game ->
                        GameGridCard(
                            game = game,
                            onClick = {
                                viewModel.onEvent(GamesLauncherUiEvent.GameClicked(game.packageName))
                                navigator.push(GameDetailScreen(game.packageName))
                            }
                        )
                    }
                }
            }
        }
    }
}