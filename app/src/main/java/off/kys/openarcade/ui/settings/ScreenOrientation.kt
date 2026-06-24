package off.kys.openarcade.ui.settings

import off.kys.openarcade.R

enum class ScreenOrientation {
    Auto, Portrait, Landscape;

    val label
        get() = when (this) {
            Auto -> "Auto"
            Portrait -> "Portrait"
            Landscape -> "Landscape"
        }
    val iconRes
        get() = when (this) {
            Auto -> R.drawable.rotate_auto_24
            Portrait -> R.drawable.round_stay_current_portrait_24
            Landscape -> R.drawable.round_stay_current_landscape_24
        }
}