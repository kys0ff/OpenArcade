package off.kys.openarcade.ui.detail

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.AsyncImage
import off.kys.openarcade.R
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class GameDetailScreen(val packageName: String) : Screen {

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: GameDetailViewModel = koinViewModel { parametersOf(packageName) }
        val game by viewModel.gameState.collectAsState()

        game?.let { currentGame ->
            val dominantColor = currentGame.getPrimaryColor()
            val onDominantColor = currentGame.getOnPrimaryColor()
            val secondaryColor = currentGame.getSecondaryColor()
            val tertiaryColor = currentGame.getTertiaryColor()

            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
            val scrollState = rememberScrollState()

            var showCategoryDialog by remember { mutableStateOf(false) }
            var newCategoryText by remember { mutableStateOf("") }
            val currentCategories = remember(currentGame.customCategories) {
                mutableStateListOf<String>().apply { addAll(currentGame.customCategories) }
            }

            val infiniteTransition = rememberInfiniteTransition(label = "halo_pulse")
            val haloAlpha by infiniteTransition.animateFloat(
                initialValue = 0.25f,
                targetValue = 0.55f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = EaseInOutSine),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "haloAlpha"
            )

            if (showCategoryDialog) {
                AlertDialog(
                    onDismissRequest = { showCategoryDialog = false },
                    title = {
                        Text(
                            text = "Categories",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            if (currentCategories.isNotEmpty()) {
                                Text(
                                    text = "Custom tags",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    currentCategories.forEach { category ->
                                        InputChip(
                                            selected = true,
                                            onClick = { currentCategories.remove(category) },
                                            label = { Text(category) },
                                            trailingIcon = {
                                                Icon(
                                                    painter = painterResource(R.drawable.round_close_24),
                                                    contentDescription = "Remove $category",
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        )
                                    }
                                }
                                HorizontalDivider()
                            } else {
                                Text(
                                    text = "No custom tags yet — add one below.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }

                            OutlinedTextField(
                                value = newCategoryText,
                                onValueChange = { newCategoryText = it },
                                label = { Text("New tag") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            val trimmed = newCategoryText.trim()
                                            if (trimmed.isNotBlank() && trimmed !in currentCategories) {
                                                currentCategories.add(trimmed)
                                                newCategoryText = ""
                                            }
                                        },
                                        enabled = newCategoryText.isNotBlank()
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.round_add_24),
                                            contentDescription = "Add tag"
                                        )
                                    }
                                }
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.updateCategories(currentCategories.toList())
                            showCategoryDialog = false
                        }) { Text("Save") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCategoryDialog = false }) { Text("Cancel") }
                    }
                )
            }

            // ─── Main Scaffold ────────────────────────────────────────────────
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                containerColor = MaterialTheme.colorScheme.background,
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = currentGame.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { navigator.pop() }) {
                                Icon(
                                    painter = painterResource(R.drawable.round_arrow_back_24),
                                    contentDescription = "Back"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
                        ),
                        scrollBehavior = scrollBehavior
                    )
                }
            ) { innerPadding ->
                Box(modifier = Modifier.fillMaxSize()) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(380.dp)
                            .background(
                                Brush.verticalGradient(
                                    colorStops = arrayOf(
                                        0.0f to dominantColor.copy(alpha = 0.20f),
                                        0.55f to dominantColor.copy(alpha = 0.06f),
                                        1.0f to Color.Transparent
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(scrollState),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // ─── Hero ─────────────────────────────────────────────
                        Spacer(Modifier.height(16.dp))

                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(172.dp)
                                    .blur(32.dp)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                dominantColor.copy(alpha = haloAlpha),
                                                Color.Transparent
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                            )

                            OutlinedCard(
                                shape = MaterialTheme.shapes.extraLarge,
                                colors = CardDefaults.outlinedCardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                ),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = Brush.linearGradient(
                                        listOf(
                                            tertiaryColor.copy(alpha = 0.55f),
                                            dominantColor.copy(alpha = 0.25f),
                                            Color.Transparent
                                        )
                                    ),
                                    width = 1.5.dp
                                ),
                                modifier = Modifier.size(136.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = currentGame.icon,
                                        contentDescription = "${currentGame.title} icon",
                                        modifier = Modifier.fillMaxSize(0.65f)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        Text(
                            text = currentGame.title,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = currentGame.packageName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 40.dp)
                        )

                        Spacer(Modifier.height(32.dp))

                        // ─── Action Row ───────────────────────────────────────
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.launchGame() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = MaterialTheme.shapes.large,
                                enabled = currentGame.isInstalled,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = dominantColor,
                                    contentColor = onDominantColor,
                                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 4.dp,
                                    pressedElevation = 1.dp
                                )
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.round_play_arrow_24),
                                    contentDescription = null,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = if (currentGame.isInstalled) "Play" else "Not Installed",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { showCategoryDialog = true },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp),
                                    shape = MaterialTheme.shapes.medium,
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = tertiaryColor
                                    ),
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = tertiaryColor.copy(alpha = 0.35f)
                                    )
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.round_add_24),
                                        contentDescription = null,
                                        modifier = Modifier.size(17.dp)
                                    )
                                    Spacer(Modifier.width(5.dp))
                                    Text(
                                        text = "Tags",
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(36.dp))

                        // ─── Details Section ──────────────────────────────────
                        SectionHeader(
                            title = "Details",
                            accentColor = secondaryColor,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                        )

                        Spacer(Modifier.height(10.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            // Enhanced multicategory layout
                            val systemCategory = if (currentGame.category.displayNameRes != 0) {
                                listOf(stringResource(currentGame.category.displayNameRes))
                            } else emptyList()

                            CategoryDetailRow(
                                label = "Categories",
                                categories = remember(currentGame.customCategories, currentGame.category) {
                                    systemCategory + currentGame.customCategories
                                },
                                accentColor = tertiaryColor,
                                isFirst = true,
                                isLast = false,
                                onClick = { showCategoryDialog = true }
                            )

                            DetailRow(
                                label = "Status",
                                value = if (currentGame.isInstalled) {
                                    stringResource(R.string.category_installed)
                                } else {
                                    stringResource(R.string.category_uninstalled)
                                },
                                valueColor = if (currentGame.isInstalled) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.error
                                },
                                isFirst = false,
                                isLast = false,
                                onClick = null
                            )

                            DetailRow(
                                label = "Package",
                                value = currentGame.packageName,
                                valueColor = MaterialTheme.colorScheme.onSurface,
                                isFirst = false,
                                isLast = true,
                                onClick = null
                            )
                        }

                        Spacer(Modifier.height(48.dp))
                    }
                }
            }

        } ?: run {
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
                        text = "Loading…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(14.dp)
                .background(
                    color = accentColor,
                    shape = MaterialTheme.shapes.extraSmall
                )
        )
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            ),
            color = accentColor
        )
    }
}

/**
 * Custom Detail Row built specifically to house responsive tags inside FlowRow.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryDetailRow(
    label: String,
    categories: List<String>,
    accentColor: Color,
    isFirst: Boolean,
    isLast: Boolean,
    onClick: () -> Unit
) {
    val topRadius = if (isFirst) 12.dp else 4.dp
    val bottomRadius = if (isLast) 12.dp else 4.dp
    val shape = RoundedCornerShape(
        topStart = topRadius, topEnd = topRadius,
        bottomStart = bottomRadius, bottomEnd = bottomRadius
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top // Align Top to handle wrapping gracefully
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp) // align with text labels inside row
            )

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Top
            ) {
                FlowRow(
                    modifier = Modifier.widthIn(max = 220.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.End),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { category ->
                        Box(
                            modifier = Modifier
                                .background(
                                    color = accentColor.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                                color = accentColor
                            )
                        }
                    }
                }

                Spacer(Modifier.width(6.dp))

                Icon(
                    painter = painterResource(R.drawable.round_chevron_left_24),
                    contentDescription = null,
                    tint = accentColor.copy(alpha = 0.45f),
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .size(15.dp)
                        .graphicsLayer { scaleX = -1f }
                )
            }
        }
    }

    if (!isLast) {
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            thickness = 0.5.dp
        )
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    valueColor: Color,
    isFirst: Boolean,
    isLast: Boolean,
    onClick: (() -> Unit)?
) {
    val topRadius = if (isFirst) 12.dp else 4.dp
    val bottomRadius = if (isLast) 12.dp else 4.dp
    val shape = RoundedCornerShape(
        topStart = topRadius, topEnd = topRadius,
        bottomStart = bottomRadius, bottomEnd = bottomRadius
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
                .padding(horizontal = 16.dp, vertical = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = valueColor,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 200.dp)
                )
                if (onClick != null) {
                    Icon(
                        painter = painterResource(R.drawable.round_chevron_left_24),
                        contentDescription = null,
                        tint = valueColor.copy(alpha = 0.45f),
                        modifier = Modifier
                            .size(15.dp)
                            .graphicsLayer { scaleX = -1f }
                    )
                }
            }
        }
    }

    if (!isLast) {
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            thickness = 0.5.dp
        )
    }
}