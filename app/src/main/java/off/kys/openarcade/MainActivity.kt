package off.kys.openarcade

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import off.kys.openarcade.ui.launcher.GamesLauncherScreen
import off.kys.openarcade.ui.theme.OpenArcadeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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