package off.kys.openarcade.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import off.kys.openarcade.ui.launcher.GamesLauncherScreen
import off.kys.openarcade.ui.launcher.GamesLauncherViewModel
import off.kys.openarcade.ui.theme.OpenArcadeTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel: GamesLauncherViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            viewModel.uiState.value.isLoading || viewModel.uiState.value.filteredGames.isEmpty()
        }

        enableEdgeToEdge()
        setContent {
            OpenArcadeTheme {
                Navigator(GamesLauncherScreen()) { navigator ->
                    SlideTransition(navigator)
                }
            }
        }
    }
}
