package off.kys.openarcade.ui.launcher

import android.content.Intent
import android.provider.Settings
import android.text.format.DateUtils
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.AsyncImage
import off.kys.openarcade.R
import off.kys.openarcade.domain.model.GameEntry
import off.kys.openarcade.domain.model.GameFilter
import off.kys.openarcade.ui.detail.GameDetailScreen
import org.koin.androidx.compose.koinViewModel
import kotlin.math.absoluteValue

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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(44.dp)
                    )
                    Text(
                        text = stringResource(R.string.searching_for_games),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
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

@Composable
private fun UsagePermissionCard(
    modifier: Modifier = Modifier,
    onGrantClick: () -> Unit
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(
                alpha = 0.1f
            )
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(
                    MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
                    Color.Transparent
                )
            )
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.permission_required_title),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = stringResource(R.string.permission_required_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onGrantClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(stringResource(R.string.grant_access))
            }
        }
    }
}

@Composable
private fun SystemStatusSection(
    batteryLevel: Int,
    storageUsage: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.system_status),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatusCard(
                title = stringResource(R.string.battery),
                value = stringResource(R.string.battery_percentage, batteryLevel),
                icon = R.drawable.round_bolt_24,
                color = if (batteryLevel > 20) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error,
                modifier = Modifier.weight(1f)
            )
            StatusCard(
                title = stringResource(R.string.storage),
                value = stringResource(R.string.storage_used, storageUsage),
                icon = R.drawable.round_explore_24,
                color = Color(0xFF2196F3),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatusCard(
    title: String,
    value: String,
    icon: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier,
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(color.copy(alpha = 0.4f), Color.Transparent))
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
private fun AnalyticsSection(games: List<GameEntry>, modifier: Modifier = Modifier) {
    val totalPlayTimeMs = games.sumOf { it.totalPlayTime }
    val totalPlayTimeHours = totalPlayTimeMs / (1000 * 60 * 60)
    val totalPlayTimeMinutes = (totalPlayTimeMs / (1000 * 60)) % 60

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.analytics),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        Color.Transparent
                    )
                )
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.round_bar_chart_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(
                            text = stringResource(R.string.total_play_time),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = stringResource(R.string.items_in_filter, games.size),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (totalPlayTimeHours > 0) {
                            stringResource(R.string.play_time_hours_minutes, totalPlayTimeHours, totalPlayTimeMinutes)
                        } else {
                            stringResource(R.string.play_time_minutes, totalPlayTimeMinutes)
                        },
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.played),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentActivitySection(
    games: List<GameEntry>,
    onGameClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.recent_activity),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
            )
            Icon(
                painter = painterResource(R.drawable.round_history_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 4.dp)
        ) {
            items(games, key = { "recent_${it.packageName}" }) { game ->
                Card(
                    onClick = { onGameClick(game.packageName) },
                    modifier = Modifier.size(64.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        AsyncImage(
                            model = game.icon,
                            contentDescription = game.title,
                            modifier = Modifier
                                .size(40.dp)
                                .padding(4.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(game.getPrimaryColor(alpha = 1.0f))
                )

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
                        text = game.title,
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
                    containerColor = game.getPrimaryColor(),
                    contentColor = game.getOnPrimaryColor(),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 20.dp, bottom = 20.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.round_play_arrow_24),
                        contentDescription = stringResource(R.string.play_game, game.title)
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
                    val activeGameColor =
                        installedGames.getOrNull(pagerState.currentPage)?.getPrimaryColor()
                            ?: Color.White

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
                val lastPlayedText = if (game.lastPlayed > 0) {
                    DateUtils.getRelativeTimeSpanString(
                        game.lastPlayed,
                        System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS
                    ).toString()
                } else {
                    stringResource(R.string.never_played)
                }

                Text(
                    text = lastPlayedText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
