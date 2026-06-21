package off.kys.openarcade.data.local

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import off.kys.openarcade.domain.model.GameCategory

class Converters {
    @TypeConverter
    fun fromCategory(category: GameCategory): String = category.name

    @TypeConverter
    fun toCategory(value: String): GameCategory = GameCategory.valueOf(value)

    @TypeConverter
    fun fromStringList(value: List<String>): String = Json.encodeToString(value)

    @TypeConverter
    fun toStringList(value: String): List<String> = try {
        Json.decodeFromString(value)
    } catch (_: Exception) {
        emptyList()
    }
}
