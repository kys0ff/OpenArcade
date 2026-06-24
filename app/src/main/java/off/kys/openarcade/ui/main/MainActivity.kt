package off.kys.openarcade.ui.main

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import off.kys.openarcade.ui.launcher.GamesLauncherScreen
import off.kys.openarcade.ui.launcher.GamesLauncherViewModel
import off.kys.openarcade.ui.settings.ScreenOrientation
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
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(uiState.immersiveMode) {
                val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
                if (uiState.immersiveMode) {
                    windowInsetsController.systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
                } else {
                    windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
                }
            }

            LaunchedEffect(uiState.keepScreenOn) {
                if (uiState.keepScreenOn) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }

            LaunchedEffect(uiState.screenOrientation) {
                requestedOrientation = when (uiState.screenOrientation) {
                    ScreenOrientation.Auto -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    ScreenOrientation.Portrait -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    ScreenOrientation.Landscape -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
            }

            OpenArcadeTheme {
                Navigator(GamesLauncherScreen()) { navigator ->
                    SlideTransition(navigator)
                }
            }
        }
    }
}
