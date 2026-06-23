package off.kys.openarcade.domain.model

import androidx.annotation.StringRes
import kotlinx.serialization.Serializable
import off.kys.openarcade.R

@Serializable
enum class GameCategory(@param:StringRes val displayNameRes: Int) {
    UNDEFINED(R.string.category_undefined),
    GAME(R.string.category_game),
    UTILITY(R.string.category_utility)
}