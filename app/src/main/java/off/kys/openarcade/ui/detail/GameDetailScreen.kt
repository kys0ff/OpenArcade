package off.kys.openarcade.ui.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import off.kys.openarcade.R
import off.kys.openarcade.ui.components.LoadingScreen
import off.kys.openarcade.ui.detail.components.GameDetailContent
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class GameDetailScreen(val packageName: String) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: GameDetailViewModel = koinViewModel { parametersOf(packageName) }
        val uiState by viewModel.uiState.collectAsState()

        val currentGame = uiState.game

        LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
            viewModel.onEvent(GameDetailUiEvent.RefreshStats)
        }

        if (currentGame != null) {
            GameDetailContent(
                uiState = uiState,
                currentGame = currentGame,
                navigator = navigator,
                onEvent = { event -> viewModel.onEvent(event) }
            )
        } else {
            LoadingScreen(message = stringResource(R.string.game_detail_loading))
        }
    }
}