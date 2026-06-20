package off.kys.openarcade.domain.model

import android.graphics.drawable.Drawable
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
    val category: String,
    val primaryColorArgb: Int,
    @Ignore @Transient val icon: Drawable? = null
) {
    // Required constructor for Room when using @Ignore on properties
    constructor(packageName: String, title: String, category: String, primaryColorArgb: Int) :
            this(packageName, title, category, primaryColorArgb, null)
}
