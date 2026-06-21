package off.kys.openarcade.ui.detail

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.AsyncImage
import com.skydoves.cloudy.cloudy
import off.kys.openarcade.R
import off.kys.openarcade.ui.components.LoadingScreen
import off.kys.openarcade.ui.components.SectionHeader
import off.kys.openarcade.ui.detail.components.CategoryDetailRow
import off.kys.openarcade.ui.detail.components.DetailRow
import off.kys.openarcade.ui.detail.components.GameCategoryDialog
import off.kys.openarcade.util.ColorExtractor
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class GameDetailScreen(val packageName: String) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: GameDetailViewModel = koinViewModel { parametersOf(packageName) }
        val uiState by viewModel.uiState.collectAsState()

        uiState.game?.let { currentGame ->
            val isDark = isSystemInDarkTheme()
            val dominantColor = remember(currentGame.primaryColorArgb, isDark) {
                ColorExtractor.getAdaptiveColor(currentGame.getPrimaryColor(), isDark)
            }
            val onDominantColor = currentGame.getOnPrimaryColor()
            val secondaryColor = remember(currentGame.secondaryColorArgb, isDark) {
                ColorExtractor.getAdaptiveColor(currentGame.getSecondaryColor(), isDark)
            }
            val tertiaryColor = remember(currentGame.tertiaryColorArgb, isDark) {
                ColorExtractor.getAdaptiveColor(currentGame.getTertiaryColor(), isDark)
            }

            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
            val scrollState = rememberScrollState()

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

            if (uiState.showCategoryDialog) {
                GameCategoryDialog(
                    editingCategories = uiState.editingCategories,
                    newCategoryDraft = uiState.newCategoryDraft,
                    accentColor = tertiaryColor,
                    onUpdateNewCategoryDraft = { viewModel.onEvent(GameDetailUiEvent.UpdateNewCategoryDraft(it)) },
                    onAddCategory = { viewModel.onEvent(GameDetailUiEvent.AddCategory(it)) },
                    onRemoveCategory = { viewModel.onEvent(GameDetailUiEvent.RemoveCategory(it)) },
                    onSave = { viewModel.onEvent(GameDetailUiEvent.SaveCategories) },
                    onDismiss = { viewModel.onEvent(GameDetailUiEvent.CloseCategoryDialog) }
                )
            }

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
                                    contentDescription = stringResource(R.string.game_detail_back_desc)
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

                        Spacer(Modifier.height(16.dp))

                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(172.dp)
                                    .cloudy(radius = 40)
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
                                        contentDescription = stringResource(R.string.game_detail_icon_desc, currentGame.title),
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

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.onEvent(GameDetailUiEvent.LaunchGame) },
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
                                    text = if (currentGame.isInstalled) stringResource(R.string.game_detail_play) else stringResource(R.string.game_detail_not_installed),
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
                                    onClick = { viewModel.onEvent(GameDetailUiEvent.OpenCategoryDialog) },
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
                                        text = stringResource(R.string.game_detail_tags_button),
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(36.dp))

                        SectionHeader(
                            title = stringResource(R.string.game_detail_section_details),
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
                            val systemCategory = if (currentGame.category.displayNameRes != 0) {
                                listOf(stringResource(currentGame.category.displayNameRes))
                            } else emptyList()

                            CategoryDetailRow(
                                label = stringResource(R.string.game_detail_label_categories),
                                categories = remember(currentGame.customCategories, currentGame.category) {
                                    systemCategory + currentGame.customCategories
                                },
                                accentColor = tertiaryColor,
                                isFirst = true,
                                isLast = false,
                                onClick = { viewModel.onEvent(GameDetailUiEvent.OpenCategoryDialog) }
                            )

                            DetailRow(
                                label = stringResource(R.string.game_detail_label_status),
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
                                label = stringResource(R.string.game_detail_label_package),
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
            LoadingScreen(message = stringResource(R.string.game_detail_loading))
        }
    }
}
