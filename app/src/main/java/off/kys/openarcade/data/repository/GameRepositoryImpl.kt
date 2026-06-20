package off.kys.openarcade.data.repository

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import off.kys.openarcade.GameScanner
import off.kys.openarcade.data.local.dao.GameDao
import off.kys.openarcade.domain.model.GameEntry
import off.kys.openarcade.domain.repository.GameRepository
import off.kys.openarcade.util.ColorExtractor

class GameRepositoryImpl(
    private val context: Context,
    private val gameDao: GameDao
) : GameRepository {

    override fun getGames(): Flow<List<GameEntry>> {
        val pm = context.packageManager
        return gameDao.getAllGames().map { entities ->
            entities.map { entity ->
                val icon = try {
                    pm.getApplicationIcon(entity.packageName)
                } catch (_: PackageManager.NameNotFoundException) {
                    null
                }
                entity.copy(icon = icon)
            }
        }
    }

    override fun getGameByPackageName(packageName: String): Flow<GameEntry?> {
        val pm = context.packageManager
        return gameDao.getGameByPackageName(packageName).map { entity ->
            entity?.let {
                val icon = try {
                    pm.getApplicationIcon(it.packageName)
                } catch (_: PackageManager.NameNotFoundException) {
                    null
                }
                it.copy(icon = icon)
            }
        }
    }

    override suspend fun refreshGames() {
        val scannedGames = GameScanner.fetchInstalledGames(context)
        val entities = scannedGames.map { game ->
            val primaryColor = ColorExtractor.extractPrimaryColor(game.icon)
            game.copy(primaryColorArgb = primaryColor.toArgb())
        }
        gameDao.deleteAll()
        gameDao.insertGames(entities)
    }
}
