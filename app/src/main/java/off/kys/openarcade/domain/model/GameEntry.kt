package off.kys.openarcade.domain.model

import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Entity(tableName = "games")
@Serializable
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
    @Ignore @Transient val icon: Drawable? = null
) {
    // Required constructor for Room when using @Ignore on properties
    constructor(
        packageName: String,
        title: String,
        category: GameCategory,
        isInstalled: Boolean = true,
        primaryColorArgb: Int,
        onPrimaryColorArgb: Int,
        secondaryColorArgb: Int,
        tertiaryColorArgb: Int,
        customCategories: List<String> = emptyList(),
        lastPlayed: Long = 0,
        totalPlayTime: Long = 0
    ) : this(
        packageName,
        title,
        category,
        isInstalled,
        primaryColorArgb,
        onPrimaryColorArgb,
        secondaryColorArgb,
        tertiaryColorArgb,
        customCategories,
        lastPlayed,
        totalPlayTime,
        null
    )

    fun getPrimaryColor(alpha: Float = 1f): Color = Color(primaryColorArgb).copy(alpha = alpha)
    fun getOnPrimaryColor(alpha: Float = 1f): Color = Color(onPrimaryColorArgb).copy(alpha = alpha)
    fun getSecondaryColor(alpha: Float = 1f): Color = Color(secondaryColorArgb).copy(alpha = alpha)
    fun getTertiaryColor(alpha: Float = 1f): Color = Color(tertiaryColorArgb).copy(alpha = alpha)
}
