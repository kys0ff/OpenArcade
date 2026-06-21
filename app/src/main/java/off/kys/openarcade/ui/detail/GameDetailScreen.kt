package off.kys.openarcade.ui.detail

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import off.kys.openarcade.R
import off.kys.openarcade.ui.components.LoadingScreen
import off.kys.openarcade.ui.detail.components.GameCategoryDialog
import off.kys.openarcade.ui.detail.components.GameDetailActions
import off.kys.openarcade.ui.detail.components.GameDetailHeader
import off.kys.openarcade.ui.detail.components.GameDetailInfoSection
import off.kys.openarcade.ui.detail.components.GameDetailTopAppBar
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
                    onUpdateNewCategoryDraft = {
                        viewModel.onEvent(
                            GameDetailUiEvent.UpdateNewCategoryDraft(
                                it
                            )
                        )
                    },
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
                    GameDetailTopAppBar(
                        title = currentGame.title,
                        onBackClick = { navigator.pop() },
                        scrollBehavior = scrollBehavior
                    )
                }
            ) { innerPadding ->
                Box(modifier = Modifier.fillMaxSize()) {

                    // Background Dynamic Gradient
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

                        GameDetailHeader(
                            currentGame = currentGame,
                            dominantColor = dominantColor,
                            tertiaryColor = tertiaryColor,
                            haloAlpha = haloAlpha
                        )

                        Spacer(Modifier.height(32.dp))

                        GameDetailActions(
                            currentGame = currentGame,
                            dominantColor = dominantColor,
                            onDominantColor = onDominantColor,
                            tertiaryColor = tertiaryColor,
                            onLaunchClick = { viewModel.onEvent(GameDetailUiEvent.LaunchGame) },
                            onTagsClick = { viewModel.onEvent(GameDetailUiEvent.OpenCategoryDialog) }
                        )

                        Spacer(Modifier.height(36.dp))

                        GameDetailInfoSection(
                            currentGame = currentGame,
                            secondaryColor = secondaryColor,
                            tertiaryColor = tertiaryColor,
                            onCategoryClick = { viewModel.onEvent(GameDetailUiEvent.OpenCategoryDialog) }
                        )

                        Spacer(Modifier.height(48.dp))
                    }
                }
            }
        } ?: run {
            LoadingScreen(message = stringResource(R.string.game_detail_loading))
        }
    }
}