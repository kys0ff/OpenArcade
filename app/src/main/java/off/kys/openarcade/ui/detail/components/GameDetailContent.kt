package off.kys.openarcade.ui.detail.components

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import off.kys.openarcade.domain.model.GameEntry
import off.kys.openarcade.ui.components.ArcadeScrollStateScrollbar
import off.kys.openarcade.ui.detail.GameDetailUiEvent
import off.kys.openarcade.ui.detail.GameDetailUiState
import off.kys.openarcade.util.ColorExtractor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailContent(
    uiState: GameDetailUiState,
    currentGame: GameEntry,
    navigator: Navigator,
    onEvent: (GameDetailUiEvent) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val dominantColor = remember(currentGame.primaryColorArgb, isDark) {
        ColorExtractor.getAdaptiveColor(currentGame.getPrimaryColor(), isDark)
    }
    val secondaryColor = remember(currentGame.secondaryColorArgb, isDark) {
        ColorExtractor.getAdaptiveColor(currentGame.getSecondaryColor(), isDark)
    }
    val tertiaryColor = remember(currentGame.tertiaryColorArgb, isDark) {
        ColorExtractor.getAdaptiveColor(currentGame.getTertiaryColor(), isDark)
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val scrollState = rememberScrollState()
    val haptic = LocalHapticFeedback.current

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
            onUpdateNewCategoryDraft = { onEvent(GameDetailUiEvent.UpdateNewCategoryDraft(it)) },
            onAddCategory = { onEvent(GameDetailUiEvent.AddCategory(it)) },
            onRemoveCategory = { onEvent(GameDetailUiEvent.RemoveCategory(it)) },
            onSave = { onEvent(GameDetailUiEvent.SaveCategories) },
            onDismiss = { onEvent(GameDetailUiEvent.CloseCategoryDialog) }
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            GameDetailTopAppBar(
                title = currentGame.displayName,
                onBackClick = {
                    if (uiState.hapticFeedback) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    navigator.pop()
                },
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

                GameDetailHeader(
                    currentGame = currentGame,
                    dominantColor = dominantColor,
                    tertiaryColor = tertiaryColor,
                    haloAlpha = haloAlpha
                )

                Spacer(Modifier.height(32.dp))

                GameDetailActions(
                    currentGame = currentGame,
                    onLaunchClick = {
                        if (uiState.hapticFeedback) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                        onEvent(GameDetailUiEvent.LaunchGame)
                    },
                    onTagsClick = {
                        if (uiState.hapticFeedback) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                        onEvent(GameDetailUiEvent.OpenCategoryDialog)
                    }
                )

                Spacer(Modifier.height(36.dp))

                GameDetailInfoSection(
                    currentGame = currentGame,
                    secondaryColor = secondaryColor,
                    tertiaryColor = tertiaryColor,
                    onCategoryClick = {
                        if (uiState.hapticFeedback) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                        onEvent(GameDetailUiEvent.OpenCategoryDialog)
                    }
                )

                Spacer(Modifier.height(48.dp))
            }

            if (uiState.showScrollbar) {
                ArcadeScrollStateScrollbar(
                    scrollState = scrollState,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxSize()
                        .padding(top = innerPadding.calculateTopPadding())
                        .padding(end = 16.dp)
                )
            }
        }
    }
}
