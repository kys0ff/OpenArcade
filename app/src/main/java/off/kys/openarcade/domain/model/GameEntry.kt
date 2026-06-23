package off.kys.openarcade.domain.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "games")
@Serializable
@Immutable
data class GameEntry(
    @PrimaryKey val packageName: String,
    val title: String,
    val category: GameCategory,
    val isInstalled: Boolean = true,
    val primaryColorArgb: Int,
    val onPrimaryColorArgb: Int,
    val secondaryColorArgb: Int,
    val tertiaryColorArgb: Int,
    val customCategories: List<String> = emptyList(),
    val lastPlayed: Long = 0,
    val totalPlayTime: Long = 0,
    val isFavorite: Boolean = false,
    val customTitle: String? = null,
    val customIconPath: String? = null,
    val cachedIconPath: String? = null,
    val lastAppUpdateTime: Long = 0,
    val isHidden: Boolean = false,
    val isManuallyAdded: Boolean = false
) {
    val displayName: String
        get() = customTitle ?: title

    fun getPrimaryColor(alpha: Float = 1f): Color = Color(primaryColorArgb).copy(alpha = alpha)
    fun getOnPrimaryColor(alpha: Float = 1f): Color = Color(onPrimaryColorArgb).copy(alpha = alpha)
    fun getSecondaryColor(alpha: Float = 1f): Color = Color(secondaryColorArgb).copy(alpha = alpha)
    fun getTertiaryColor(alpha: Float = 1f): Color = Color(tertiaryColorArgb).copy(alpha = alpha)
}
