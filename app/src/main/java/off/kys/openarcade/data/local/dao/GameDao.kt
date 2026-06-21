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

    @Query("SELECT * FROM games")
    suspend fun getAllGamesSync(): List<GameEntry>

    @Query("UPDATE games SET isInstalled = 0 WHERE packageName NOT IN (:presentPackageNames)")
    suspend fun markMissingAsUninstalled(presentPackageNames: List<String>)

    @Query("DELETE FROM games")
    suspend fun deleteAll()
}
