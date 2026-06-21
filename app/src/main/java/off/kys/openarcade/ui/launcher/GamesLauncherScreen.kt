package off.kys.openarcade.ui.launcher

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.AsyncImage
import off.kys.openarcade.R
import off.kys.openarcade.domain.model.GameEntry
import off.kys.openarcade.ui.detail.GameDetailScreen
import org.koin.androidx.compose.koinViewModel
import kotlin.math.absoluteValue

class GamesLauncherScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: GamesLauncherViewModel = koinViewModel()
        val allGames by viewModel.allGames.collectAsState()
        val filters by viewModel.availableFilters.collectAsState()

        var selectedFilterIndex by remember { mutableIntStateOf(0) }
        var selectedTab by remember { mutableIntStateOf(0) }

        val safeFilterIndex = if (selectedFilterIndex >= filters.size) 0 else selectedFilterIndex
        val selectedFilter = filters.getOrElse(safeFilterIndex) { GameFilter.All }

        val filteredGames = remember(allGames, selectedFilter) {
            when (selectedFilter) {
                is GameFilter.All -> allGames
                is GameFilter.Installed -> allGames.filter { it.isInstalled }
                is GameFilter.Uninstalled -> allGames.filter { !it.isInstalled }
                is GameFilter.System -> allGames.filter { it.category == selectedFilter.category }
                is GameFilter.Custom -> allGames.filter { selectedFilter.name in it.customCategories }
            }
        }

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                    val tabs = listOf(
                        Pair("Library", R.drawable.round_sports_esports_24),
                        Pair("Dashboard", R.drawable.round_dashboard_24),
                        Pair("Explore", R.drawable.round_explore_24)
                    )
                    tabs.forEachIndexed { index, (title, icon) ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            icon = {
                                Icon(
                                    painter = painterResource(icon),
                                    contentDescription = title
                                )
                            },
                            label = { Text(title) }
                        )
                    }
                }
            }
        ) { innerPadding ->
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

                // ─── Hero Pager ───────────────────────────────────────────────
                if (filteredGames.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        HeroBannerPager(
                            installedGames = filteredGames,
                            onInspectGame = { pkg -> navigator.push(GameDetailScreen(pkg)) },
                            modifier = Modifier.layout { measurable, constraints ->
                                // Bleed past the grid's horizontal padding
                                val bleed = 16.dp.roundToPx()
                                val placeable = measurable.measure(
                                    constraints.copy(maxWidth = constraints.maxWidth + bleed * 2)
                                )
                                layout(constraints.maxWidth, placeable.height) {
                                    placeable.placeRelative(-bleed, 0)
                                }
                            }
                        )
                    }
                } else {
                    // ─── Empty State ──────────────────────────────────────────
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
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.size(56.dp)
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "No games here",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Install a game or try a different filter.",
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
                        // Padding at the start and end ensures chips don't look glued to the screen edges while scrolling
                        contentPadding = PaddingValues(vertical = 8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(
                            count = filters.size,
                            key = { index ->
                                // Providing a unique key stops the list from aggressively flashing during state changes
                                when (val filter = filters[index]) {
                                    is GameFilter.All -> "all"
                                    is GameFilter.Installed -> "installed"
                                    is GameFilter.Uninstalled -> "uninstalled"
                                    is GameFilter.System -> "system_${filter.category.name}"
                                    is GameFilter.Custom -> "custom_${filter.name}"
                                }
                            }
                        ) { index ->
                            val isSelected = safeFilterIndex == index
                            val filter = filters[index]

                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedFilterIndex = index },
                                label = {
                                    Text(
                                        text = when (filter) {
                                            is GameFilter.All -> "All Games"
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
                                // A subtle checkmark or custom indicator makes selection obvious instantly
                                leadingIcon = if (isSelected) {
                                    {
                                        Icon(
                                            painter = painterResource(R.drawable.round_check_24),
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                } else null,
                                shape = CircleShape, // Fully rounded pill shapes look much cleaner in modern dashboards
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
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

                if (filteredGames.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Your library",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "${filteredGames.size} games",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                items(filteredGames, key = { it.packageName }) { game ->
                    GameGridCard(
                        game = game,
                        onClick = { navigator.push(GameDetailScreen(game.packageName)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroBannerPager(
    modifier: Modifier = Modifier,
    installedGames: List<GameEntry>,
    onInspectGame: (String) -> Unit
) {
    val pagerCount = minOf(installedGames.size, 5)
    val pagerState = rememberPagerState { pagerCount }

    Box(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) { page ->
            val game = installedGames[page]
            val pageOffset by remember(pagerState.currentPage) {
                derivedStateOf { (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                // Base color fill
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(game.getPrimaryColor(alpha = 1.0f))
                )

                // Blown-up icon with parallax
                AsyncImage(
                    model = game.icon,
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

                // Bottom cinema gradient
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

                // Title + categories (Dots removed from here)
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        // Extra bottom padding to leave dedicated room for the static dots below it
                        .padding(start = 24.dp, end = 88.dp, bottom = 44.dp)
                ) {
                    Text(
                        text = game.title,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(3.dp))
                    val categoryStrings = game.customCategories + if (game.category.displayNameRes != 0) {
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

                // FAB
                FloatingActionButton(
                    onClick = { onInspectGame(game.packageName) },
                    containerColor = game.getPrimaryColor(),
                    contentColor = game.getOnPrimaryColor(),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 20.dp, bottom = 20.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.round_play_arrow_24),
                        contentDescription = "Play ${game.title}"
                    )
                }
            }
        }

        // Static Pager Dots — Placed outside HorizontalPager so they don't slide
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

                    // Pull the game color context for the active dot dynamically from the active page
                    val activeGameColor = installedGames.getOrNull(pagerState.currentPage)?.getPrimaryColor() ?: Color.White

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

// ─── Game Grid Card ───────────────────────────────────────────────────────────

@Composable
private fun GameGridCard(
    game: GameEntry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        onClick = onClick,
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(
                    game.getTertiaryColor(alpha = 0.45f),
                    Color.Transparent
                )
            )
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            // Icon well — colored gradient derived from game palette
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.25f)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                game.getPrimaryColor(alpha = 0.18f),
                                game.getPrimaryColor(alpha = 0.04f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = MaterialTheme.shapes.small,
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    AsyncImage(
                        model = game.icon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(52.dp)
                            .padding(4.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // Thin color accent strip — game identity marker
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                game.getPrimaryColor(alpha = 0.70f),
                                game.getTertiaryColor(alpha = 0.35f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Title + category
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
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
                val categoryStrings = game.customCategories + if (game.category.displayNameRes != 0) {
                    listOf(stringResource(game.category.displayNameRes))
                } else emptyList()
                Text(
                    text = categoryStrings.joinToString(" · "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}