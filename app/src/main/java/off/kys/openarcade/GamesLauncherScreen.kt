package off.kys.openarcade

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.AsyncImage
import off.kys.openarcade.ui.launcher.GamesLauncherViewModel
import org.koin.androidx.compose.koinViewModel

class GamesLauncherScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: GamesLauncherViewModel = koinViewModel()
        val installedGames by viewModel.installedGames.collectAsState()

        val filters = listOf("All Games", "Installed", "Performance Mode")
        var selectedFilter by remember { mutableIntStateOf(0) }
        var selectedTab by remember { mutableIntStateOf(0) }

        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = Color.Transparent,
                    tonalElevation = 0.dp,
                    modifier = Modifier.background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.surfaceContainerHigh
                            )
                        )
                    )
                ) {
                    val tabs = listOf("Library", "Dashboard", "Explore")
                    tabs.forEachIndexed { index, title ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            icon = {
                                Icon(
                                    painter = painterResource(
                                        id = when (index) {
                                            0 -> R.drawable.round_sports_esports_24
                                            1 -> R.drawable.round_dashboard_24
                                            else -> R.drawable.round_explore_24
                                        }
                                    ),
                                    contentDescription = title
                                )
                            },
                            label = { Text(title) }
                        )
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding()),
                contentPadding = PaddingValues(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // 1. Immersive Hero Banner Space
                item(span = { GridItemSpan(maxLineSpan) }) {
                    if (installedGames.isNotEmpty()) {
                        val pagerState =
                            rememberPagerState { minOf(installedGames.size, 3) }

                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        ) { page ->
                            val game = installedGames[page]

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(game.primaryColorArgb).copy(alpha = 0.5f))
                            ) {
                                // Draw application identity blown up on background layout
                                AsyncImage(
                                    model = game.icon,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                    alpha = 0.2f
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color.Black.copy(alpha = 0.9f)
                                                )
                                            )
                                        )
                                )

                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(24.dp)
                                ) {
                                    Text(
                                        text = game.title,
                                        style = MaterialTheme.typography.headlineLarge,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = game.packageName,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.LightGray
                                    )
                                }

                                FloatingActionButton(
                                    onClick = { navigator.push(GameDetailScreen(game.packageName)) },
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(24.dp)
                                ) {
                                    Icon(
                                        painterResource(R.drawable.round_play_arrow_24),
                                        contentDescription = "Inspect"
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Navigation Horizontal Filter Row
                item(span = { GridItemSpan(maxLineSpan) }) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(filters.size) { index ->
                            FilterChip(
                                selected = selectedFilter == index,
                                onClick = { selectedFilter = index },
                                label = { Text(filters[index]) }
                            )
                        }
                    }
                }

                // 3. Grid Row Header Text
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Verified Hardware Library",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                // 4. Interactive Application Cards
                items(installedGames) { game ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = if (installedGames.indexOf(game) % 2 == 0) 16.dp else 0.dp,
                                end = if ((installedGames.indexOf(game) % 2) != 0) 16.dp else 0.dp
                            ),
                        shape = MaterialTheme.shapes.large,
                        onClick = { navigator.push(GameDetailScreen(game.packageName)) }
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1.2f)
                                    .background(Color(game.primaryColorArgb).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = game.icon,
                                    contentDescription = game.title,
                                    modifier = Modifier.size(64.dp),
                                )
                            }

                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = game.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = game.category,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}