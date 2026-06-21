package off.kys.openarcade.domain.repository

import kotlinx.coroutines.flow.Flow
import off.kys.openarcade.domain.model.GameEntry

interface GameRepository {
    fun getGames(): Flow<List<GameEntry>>
    fun getGameByPackageName(packageName: String): Flow<GameEntry?>
    suspend fun refreshGames()
    suspend fun updateCustomCategories(packageName: String, customCategories: List<String>)
}
