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

    @Query("DELETE FROM games")
    suspend fun deleteAll()
}
