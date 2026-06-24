package off.kys.openarcade.ui.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import off.kys.openarcade.R
import off.kys.openarcade.domain.model.LauncherSection
import off.kys.openarcade.ui.components.ArcadeToggle
import off.kys.openarcade.ui.components.ScrollbarWidth
import off.kys.openarcade.ui.components.SectionHeader
import off.kys.openarcade.ui.theme.OpenArcadeTheme
import org.koin.androidx.compose.koinViewModel

private val SettingItemHeight = 64.dp
private val SettingHPad = 16.dp
private val SettingIconBadgeSize = 38.dp
private val SettingIconSize = 20.dp
private val SettingIconSpacing = 14.dp
private const val AnimDuration = 200

class SettingsScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: SettingsViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val listState = rememberLazyListState()
        val primary = MaterialTheme.colorScheme.primary
        val tertiary = MaterialTheme.colorScheme.tertiary

        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Settings",
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
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                    // 2dp accent bar — mirrors AppPickerScreen / GameGridCard
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 32.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxSize()
                ) {

                    item {
                        SectionHeader(title = "Display")
                        Spacer(Modifier.height(8.dp))
                    }

                    item {
                        SettingsGroup {
                            ToggleSettingItem(
                                icon = painterResource(R.drawable.round_bolt_24),
                                title = "Immersive Mode",
                                subtitle = "Hide status & navigation bars while in launcher",
                                checked = state.immersiveMode,
                                hapticFeedbackEnabled = state.hapticFeedback,
                                onCheckedChange = { viewModel.onEvent(SettingsUiEvent.ToggleImmersiveMode(it)) }
                            )
                            SettingsGroupDivider()
                            ToggleSettingItem(
                                icon = painterResource(R.drawable.round_favorite_24),
                                title = "Keep Screen On",
                                subtitle = "Prevent screen from sleeping in launcher",
                                checked = state.keepScreenOn,
                                hapticFeedbackEnabled = state.hapticFeedback,
                                onCheckedChange = { viewModel.onEvent(SettingsUiEvent.ToggleKeepScreenOn(it)) }
                            )
                            SettingsGroupDivider()
                            ToggleSettingItem(
                                icon = painterResource(R.drawable.round_explore_24),
                                title = "Show Scrollbar",
                                subtitle = "Arcade-styled gradient scrollbar on lists",
                                checked = state.showScrollbar,
                                hapticFeedbackEnabled = state.hapticFeedback,
                                onCheckedChange = { viewModel.onEvent(SettingsUiEvent.ToggleShowScrollbar(it)) }
                            )
                        }
                    }

                    // ── Screen Orientation ────────────────────────────────────
                    item { Spacer(Modifier.height(20.dp)) }
                    item {
                        SectionHeader(title = "Screen Orientation")
                        Spacer(Modifier.height(8.dp))
                    }
                    item {
                        SettingsGroup {
                            OrientationSettingItem(
                                selected = state.screenOrientation,
                                hapticFeedbackEnabled = state.hapticFeedback,
                                onSelect = { viewModel.onEvent(SettingsUiEvent.SetScreenOrientation(it)) }
                            )
                        }
                    }

                    // ── Grid Layout ───────────────────────────────────────────
                    item { Spacer(Modifier.height(20.dp)) }
                    item {
                        SectionHeader(title = "Grid Layout")
                        Spacer(Modifier.height(8.dp))
                    }
                    item {
                        SettingsGroup {
                            GridColumnsSettingItem(
                                selected = state.gridColumns,
                                hapticFeedbackEnabled = state.hapticFeedback,
                                onSelect = { viewModel.onEvent(SettingsUiEvent.SetGridColumns(it)) }
                            )
                        }
                    }

                    // ── Launcher Sections ─────────────────────────────────────
                    item { Spacer(Modifier.height(20.dp)) }
                    item {
                        SectionHeader(title = "Launcher Sections")
                        Spacer(Modifier.height(8.dp))
                    }
                    item {
                        SettingsGroup {
                            ToggleSettingItem(
                                icon = painterResource(R.drawable.round_favorite_24),
                                title = "Favorites",
                                subtitle = "Show pinned games at the top",
                                checked = state.showFavoritesSection,
                                hapticFeedbackEnabled = state.hapticFeedback,
                                onCheckedChange = { viewModel.onEvent(SettingsUiEvent.ToggleSection(LauncherSection.FAVORITES, it)) }
                            )
                            SettingsGroupDivider()
                            ToggleSettingItem(
                                icon = painterResource(R.drawable.round_bar_chart_24),
                                title = "Analytics",
                                subtitle = "Show play-time charts and stats",
                                checked = state.showAnalyticsSection,
                                hapticFeedbackEnabled = state.hapticFeedback,
                                onCheckedChange = { viewModel.onEvent(SettingsUiEvent.ToggleSection(LauncherSection.ANALYTICS, it)) }
                            )
                            SettingsGroupDivider()
                            ToggleSettingItem(
                                icon = painterResource(R.drawable.round_history_24),
                                title = "Recent Activity",
                                subtitle = "Show recently played games",
                                checked = state.showRecentSection,
                                hapticFeedbackEnabled = state.hapticFeedback,
                                onCheckedChange = { viewModel.onEvent(SettingsUiEvent.ToggleSection(LauncherSection.RECENT_ACTIVITY, it)) }
                            )
                            SettingsGroupDivider()
                            ToggleSettingItem(
                                icon = painterResource(R.drawable.round_bolt_24),
                                title = "System Status",
                                subtitle = "Show battery, storage and RAM usage",
                                checked = state.showSystemStatus,
                                hapticFeedbackEnabled = state.hapticFeedback,
                                onCheckedChange = { viewModel.onEvent(SettingsUiEvent.ToggleSection(LauncherSection.SYSTEM_STATUS, it)) }
                            )
                        }
                    }

                    // ── Feel ──────────────────────────────────────────────────
                    item { Spacer(Modifier.height(20.dp)) }
                    item {
                        SectionHeader(title = "Feel")
                        Spacer(Modifier.height(8.dp))
                    }
                    item {
                        SettingsGroup {
                            ToggleSettingItem(
                                icon = painterResource(R.drawable.round_sports_esports_24),
                                title = "Haptic Feedback",
                                subtitle = "Vibrate on button presses and game launch",
                                checked = state.hapticFeedback,
                                hapticFeedbackEnabled = state.hapticFeedback,
                                onCheckedChange = { viewModel.onEvent(SettingsUiEvent.ToggleHapticFeedback(it)) }
                            )
                            SettingsGroupDivider()
                            ToggleSettingItem(
                                icon = painterResource(R.drawable.round_explore_24),
                                title = "Launch Animation",
                                subtitle = "Animate app icon when launching a game",
                                checked = state.launchAnimation,
                                hapticFeedbackEnabled = state.hapticFeedback,
                                onCheckedChange = { viewModel.onEvent(SettingsUiEvent.ToggleLaunchAnimation(it)) }
                            )
                            SettingsGroupDivider()
                            ToggleSettingItem(
                                icon = painterResource(R.drawable.round_category_24),
                                title = "Reduce Animations",
                                subtitle = "Simplify UI transitions for performance",
                                checked = state.reduceAnimations,
                                hapticFeedbackEnabled = state.hapticFeedback,
                                onCheckedChange = { viewModel.onEvent(SettingsUiEvent.ToggleReduceAnimations(it)) }
                            )
                        }
                    }

                    item { Spacer(Modifier.height(20.dp)) }
                    item {
                        SectionHeader(title = "About")
                        Spacer(Modifier.height(8.dp))
                    }
                    item {
                        SettingsGroup {
                            NavigationSettingItem(
                                icon = painterResource(R.drawable.round_sports_esports_24),
                                title = "OpenArcade",
                                subtitle = "Version 1.0.0 · Built with ♥",
                                hapticFeedbackEnabled = state.hapticFeedback,
                                onClick = {}
                            )
                            SettingsGroupDivider()
                            NavigationSettingItem(
                                icon = painterResource(R.drawable.round_explore_24),
                                title = "Licenses",
                                subtitle = "Open source libraries used in this app",
                                hapticFeedbackEnabled = state.hapticFeedback,
                                onClick = {}
                            )
                            SettingsGroupDivider()
                            NavigationSettingItem(
                                icon = painterResource(R.drawable.round_close_24),
                                title = "Reset All Settings",
                                subtitle = "Restore defaults",
                                isDestructive = true,
                                hapticFeedbackEnabled = state.hapticFeedback,
                                onClick = { viewModel.onEvent(SettingsUiEvent.ResetAll) }
                            )
                        }
                    }

                    item { Spacer(Modifier.height(16.dp)) }
                }

                // Arcade scrollbar overlay
                if (state.showScrollbar) {
                    ArcadeScrollbar(
                        listState = listState,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxSize()
                            .padding(end = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsGroup(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .border(
                1.dp,
                Brush.linearGradient(
                    listOf(
                        tertiary.copy(alpha = 0.45f),
                        primary.copy(alpha = 0.20f),
                        Color.Transparent
                    )
                ),
                MaterialTheme.shapes.large
            )
    ) {
        // 2dp top accent bar
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
        content()
    }
}

/** Thin gradient divider between items inside a [SettingsGroup]. */
@Composable
private fun SettingsGroupDivider() {
    Box(
        modifier = Modifier
            .padding(start = SettingHPad + SettingIconBadgeSize + SettingIconSpacing)
            .fillMaxWidth()
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.07f),
                        Color.Transparent
                    )
                )
            )
    )
}

@Composable
private fun ToggleSettingItem(
    icon: Painter,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    hapticFeedbackEnabled: Boolean = true
) {
    val haptic = LocalHapticFeedback.current
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val rowBackground by animateColorAsState(
        targetValue = if (isPressed)
            primary.copy(alpha = if (checked) 0.12f else 0.06f)
        else Color.Transparent,
        animationSpec = tween(AnimDuration),
        label = "toggleRowBg_$title"
    )

    val iconBadgeBg by animateColorAsState(
        targetValue = if (checked) primary.copy(alpha = 0.18f)
        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
        animationSpec = tween(AnimDuration),
        label = "iconBadgeBg_$title"
    )
    val iconTint by animateColorAsState(
        targetValue = if (checked) primary
        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.50f),
        animationSpec = tween(AnimDuration),
        label = "iconTint_$title"
    )
    val titleColor by animateColorAsState(
        targetValue = if (checked) MaterialTheme.colorScheme.onSurface
        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
        animationSpec = tween(AnimDuration),
        label = "titleColor_$title"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(SettingItemHeight)
            .background(rowBackground)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    if (hapticFeedbackEnabled) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    onCheckedChange(!checked)
                }
            )
            .padding(horizontal = SettingHPad),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SettingIconSpacing)
    ) {
        // Icon badge
        SettingIconBadge(
            icon = icon,
            tint = iconTint,
            background = Brush.radialGradient(
                listOf(
                    iconBadgeBg,
                    if (checked) tertiary.copy(alpha = 0.04f) else iconBadgeBg.copy(alpha = 0.3f)
                )
            ),
            borderColor = iconTint.copy(alpha = 0.40f)
        )

        // Labels
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (checked) FontWeight.SemiBold else FontWeight.Normal
                ),
                color = titleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                // primary for meta text — same convention as GameGridCard
                color = MaterialTheme.colorScheme.primary.copy(
                    alpha = if (checked) 0.80f else 0.45f
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        ArcadeToggle(
            checked = checked,
            onCheckedChange = onCheckedChange,
            hapticFeedbackEnabled = hapticFeedbackEnabled
        )
    }
}

// ─── Orientation picker item ──────────────────────────────────────────────────

@Composable
private fun OrientationSettingItem(
    selected: ScreenOrientation,
    onSelect: (ScreenOrientation) -> Unit,
    hapticFeedbackEnabled: Boolean = true
) {
    val haptic = LocalHapticFeedback.current
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SettingHPad, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SettingIconSpacing)
        ) {
            SettingIconBadge(
                icon = painterResource(ScreenOrientation.Auto.iconRes),
                tint = primary,
                background = Brush.radialGradient(
                    listOf(primary.copy(alpha = 0.18f), primary.copy(alpha = 0.04f))
                ),
                borderColor = primary.copy(alpha = 0.40f)
            )
            Column {
                Text(
                    text = "Screen Orientation",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Controls how the launcher rotates",
                    style = MaterialTheme.typography.bodySmall,
                    color = primary.copy(alpha = 0.70f)
                )
            }
        }

        // Orientation chip row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ScreenOrientation.entries.forEach { orientation ->
                val isSelected = selected == orientation
                val chipContainer by animateColorAsState(
                    targetValue = if (isSelected)
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.72f)
                    else MaterialTheme.colorScheme.surfaceContainerLow,
                    animationSpec = tween(AnimDuration),
                    label = "oriChip_$orientation"
                )
                val chipLabel by animateColorAsState(
                    targetValue = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    animationSpec = tween(AnimDuration),
                    label = "oriLabel_$orientation"
                )
                val chipBorderStart by animateColorAsState(
                    targetValue = if (isSelected) primary.copy(alpha = 0.85f)
                    else tertiary.copy(alpha = 0.30f),
                    animationSpec = tween(AnimDuration),
                    label = "oriBorder_$orientation"
                )

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(chipContainer, MaterialTheme.shapes.medium)
                        .border(
                            1.dp,
                            Brush.linearGradient(
                                listOf(chipBorderStart, Color.Transparent)
                            ),
                            MaterialTheme.shapes.medium
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                if (hapticFeedbackEnabled) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                                onSelect(orientation)
                            }
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(orientation.iconRes),
                        contentDescription = null,
                        tint = chipLabel,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = orientation.label,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold
                            else FontWeight.Medium
                        ),
                        color = chipLabel
                    )
                }
            }
        }
    }
}

// ─── Grid column picker item ──────────────────────────────────────────────────

@Composable
private fun GridColumnsSettingItem(
    selected: GridColumns,
    onSelect: (GridColumns) -> Unit,
    hapticFeedbackEnabled: Boolean = true
) {
    val haptic = LocalHapticFeedback.current
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SettingHPad, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SettingIconSpacing)
        ) {
            SettingIconBadge(
                icon = painterResource(R.drawable.round_category_24),
                tint = primary,
                background = Brush.radialGradient(
                    listOf(primary.copy(alpha = 0.18f), primary.copy(alpha = 0.04f))
                ),
                borderColor = primary.copy(alpha = 0.40f)
            )
            Column {
                Text(
                    text = "Grid Columns",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Games per row on the launcher grid",
                    style = MaterialTheme.typography.bodySmall,
                    color = primary.copy(alpha = 0.70f)
                )
            }
        }

        // Column count selector
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            GridColumns.entries.forEach { col ->
                val isSelected = selected == col
                val container by animateColorAsState(
                    targetValue = if (isSelected)
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.72f)
                    else MaterialTheme.colorScheme.surfaceContainerLow,
                    animationSpec = tween(AnimDuration),
                    label = "gridChip_$col"
                )
                val labelColor by animateColorAsState(
                    targetValue = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    animationSpec = tween(AnimDuration),
                    label = "gridLabel_$col"
                )

                // Visual mini grid preview
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(container, MaterialTheme.shapes.medium)
                        .border(
                            1.dp,
                            Brush.linearGradient(
                                listOf(
                                    if (isSelected) primary.copy(alpha = 0.85f)
                                    else tertiary.copy(alpha = 0.30f),
                                    Color.Transparent
                                )
                            ),
                            MaterialTheme.shapes.medium
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                if (hapticFeedbackEnabled) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                                onSelect(col)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Mini dot grid preview
                        val dotSize = 6.dp
                        val dotSpacing = 3.dp
                        repeat(2) {
                            Row(horizontalArrangement = Arrangement.spacedBy(dotSpacing)) {
                                repeat(col.count) {
                                    Box(
                                        modifier = Modifier
                                            .size(dotSize)
                                            .clip(MaterialTheme.shapes.extraSmall)
                                            .background(labelColor.copy(alpha = 0.55f))
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = col.count.toString(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold
                                else FontWeight.Medium
                            ),
                            color = labelColor
                        )
                    }
                }
            }
        }
    }
}

// ─── Navigation item ──────────────────────────────────────────────────────────

@Composable
private fun NavigationSettingItem(
    icon: Painter,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false,
    hapticFeedbackEnabled: Boolean = true
) {
    val haptic = LocalHapticFeedback.current
    val primary = MaterialTheme.colorScheme.primary
    val error = MaterialTheme.colorScheme.error
    val accent = if (isDestructive) error else primary

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val rowBackground by animateColorAsState(
        targetValue = if (isPressed) accent.copy(alpha = 0.10f) else Color.Transparent,
        animationSpec = tween(AnimDuration),
        label = "navRowBg_$title"
    )
    val iconTint by animateColorAsState(
        targetValue = if (isPressed) accent else accent.copy(alpha = 0.65f),
        animationSpec = tween(AnimDuration),
        label = "navIconTint_$title"
    )
    val chevronAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.70f else 0.30f,
        animationSpec = tween(AnimDuration),
        label = "navChevron_$title"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(SettingItemHeight)
            .background(rowBackground)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    if (hapticFeedbackEnabled) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    onClick()
                }
            )
            .padding(horizontal = SettingHPad),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SettingIconSpacing)
    ) {
        SettingIconBadge(
            icon = icon,
            tint = iconTint,
            background = Brush.radialGradient(
                listOf(accent.copy(alpha = 0.15f), accent.copy(alpha = 0.04f))
            ),
            borderColor = iconTint.copy(alpha = 0.35f)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = if (isDestructive) error
                else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = accent.copy(alpha = 0.65f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Trailing chevron
        Icon(
            painter = painterResource(R.drawable.round_arrow_back_24),
            contentDescription = null,
            tint = accent.copy(alpha = chevronAlpha),
            modifier = Modifier
                .size(14.dp)
                .alpha(chevronAlpha)
                // Rotate the back arrow to point right
                .graphicsLayer(rotationZ = 180f)
        )
    }
}

// ─── Icon badge ───────────────────────────────────────────────────────────────

/** Reusable icon badge — radial wash + gradient border, same as GameActionItem. */
@Composable
private fun SettingIconBadge(
    icon: Painter,
    tint: Color,
    background: Brush,
    borderColor: Color
) {
    Box(
        modifier = Modifier
            .size(SettingIconBadgeSize)
            .clip(MaterialTheme.shapes.small)
            .background(background)
            .border(
                1.dp,
                Brush.linearGradient(listOf(borderColor, Color.Transparent)),
                MaterialTheme.shapes.small
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(SettingIconSize)
        )
    }
}

/**
 * Gradient scrollbar overlay that reads from [LazyListState].
 * Mirrors the primary→tertiary language; fades out when the list is at rest.
 */
@Composable
fun ArcadeScrollbar(
    listState: LazyListState,
    modifier: Modifier = Modifier,
    width: Dp = ScrollbarWidth
) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary

    val isScrolling = listState.isScrollInProgress
    val thumbAlpha by animateFloatAsState(
        targetValue = if (isScrolling) 0.85f else 0.30f,
        animationSpec = tween(if (isScrolling) 100 else 600),
        label = "scrollbarAlpha"
    )

    Box(
        modifier = modifier.drawWithContent {
            drawContent()

            val totalItems = listState.layoutInfo.totalItemsCount
            if (totalItems == 0) return@drawWithContent

            val visibleItems = listState.layoutInfo.visibleItemsInfo
            val firstVisible = visibleItems.firstOrNull() ?: return@drawWithContent

            val viewportH = size.height
            val thumbH = (viewportH * (visibleItems.size.toFloat() / totalItems))
                .coerceAtLeast(40.dp.toPx())
            val scrollFraction = firstVisible.index.toFloat() / (totalItems - visibleItems.size)
                .coerceAtLeast(1)
            val thumbY = (scrollFraction * (viewportH - thumbH)).coerceIn(0f, viewportH - thumbH)

            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(
                        primary.copy(alpha = thumbAlpha),
                        tertiary.copy(alpha = thumbAlpha * 0.60f)
                    )
                ),
                topLeft = Offset(size.width - width.toPx(), thumbY),
                size = Size(width.toPx(), thumbH),
                cornerRadius = CornerRadius(width.toPx() / 2)
            )
        }
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun SettingsScreenPreview() {
    OpenArcadeTheme {
        var state by remember { mutableStateOf(SettingsState()) }
        val listState = rememberLazyListState()

        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                item {
                    SectionHeader("Display")
                    Spacer(Modifier.height(8.dp))
                }
                item {
                    SettingsGroup {
                        ToggleSettingItem(
                            icon = painterResource(R.drawable.round_bolt_24),
                            title = "Immersive Mode",
                            subtitle = "Hide status & navigation bars",
                            checked = state.immersiveMode,
                            onCheckedChange = { state = state.copy(immersiveMode = it) }
                        )
                        SettingsGroupDivider()
                        ToggleSettingItem(
                            icon = painterResource(R.drawable.round_explore_24),
                            title = "Show Scrollbar",
                            subtitle = "Arcade-styled gradient scrollbar",
                            checked = state.showScrollbar,
                            onCheckedChange = { state = state.copy(showScrollbar = it) }
                        )
                    }
                }
                item { Spacer(Modifier.height(20.dp)) }
                item {
                    SectionHeader("Screen Orientation")
                    Spacer(Modifier.height(8.dp))
                }
                item {
                    SettingsGroup {
                        OrientationSettingItem(
                            selected = state.screenOrientation,
                            onSelect = { state = state.copy(screenOrientation = it) }
                        )
                    }
                }
                item { Spacer(Modifier.height(20.dp)) }
                item {
                    SectionHeader("Grid Layout")
                    Spacer(Modifier.height(8.dp))
                }
                item {
                    SettingsGroup {
                        GridColumnsSettingItem(
                            selected = state.gridColumns,
                            onSelect = { state = state.copy(gridColumns = it) }
                        )
                    }
                }
            }
            ArcadeScrollbar(
                listState = listState,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxSize()
                    .padding(end = 16.dp)
            )
        }
    }
}
