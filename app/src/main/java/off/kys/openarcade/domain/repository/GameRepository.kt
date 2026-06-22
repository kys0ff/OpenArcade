package off.kys.openarcade.domain.repository

import kotlinx.coroutines.flow.Flow
import off.kys.openarcade.domain.model.GameEntry

interface GameRepository {
    fun getGames(): Flow<List<GameEntry>>
    fun getGameByPackageName(packageName: String): Flow<GameEntry?>
    suspend fun refreshGames()
    suspend fun refreshGameStats(packageName: String)
    suspend fun refreshAllGameStats()
    suspend fun updateCustomCategories(packageName: String, customCategories: List<String>)
    suspend fun updateFavoriteStatus(packageName: String, isFavorite: Boolean)
    suspend fun updateCustomTitle(packageName: String, customTitle: String?)
    suspend fun updateCustomIconPath(packageName: String, customIconPath: String?)
    suspend fun updateVisibility(packageName: String, isHidden: Boolean)
    suspend fun addGame(game: GameEntry)
    fun getAnalyticsData(): Flow<off.kys.openarcade.domain.model.AnalyticsData>
}
