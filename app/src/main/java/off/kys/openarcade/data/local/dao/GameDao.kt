package off.kys.openarcade.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import off.kys.openarcade.domain.model.GameEntry

@Dao
interface GameDao {
    @Query("SELECT * FROM games")
    fun getAllGames(): Flow<List<GameEntry>>

    @Query("SELECT * FROM games WHERE packageName = :packageName")
    fun getGameByPackageName(packageName: String): Flow<GameEntry?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<GameEntry>)

    @Query("UPDATE games SET customCategories = :customCategories WHERE packageName = :packageName")
    suspend fun updateCustomCategories(packageName: String, customCategories: List<String>)

    @Query("UPDATE games SET lastPlayed = :lastPlayed, totalPlayTime = :totalPlayTime WHERE packageName = :packageName")
    suspend fun updatePlayStats(packageName: String, lastPlayed: Long, totalPlayTime: Long)

    @Query("UPDATE games SET isFavorite = :isFavorite WHERE packageName = :packageName")
    suspend fun updateFavoriteStatus(packageName: String, isFavorite: Boolean)

    @Query("UPDATE games SET customTitle = :customTitle WHERE packageName = :packageName")
    suspend fun updateCustomTitle(packageName: String, customTitle: String?)

    @Query("UPDATE games SET customIconPath = :customIconPath WHERE packageName = :packageName")
    suspend fun updateCustomIconPath(packageName: String, customIconPath: String?)

    @Query("UPDATE games SET isHidden = :isHidden WHERE packageName = :packageName")
    suspend fun updateVisibility(packageName: String, isHidden: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameEntry)

    @Query("SELECT * FROM games")
    suspend fun getAllGamesSync(): List<GameEntry>

    @Query("UPDATE games SET isInstalled = 0 WHERE packageName NOT IN (:presentPackageNames)")
    suspend fun markMissingAsUninstalled(presentPackageNames: List<String>)

    @Query("DELETE FROM games")
    suspend fun deleteAll()
}
