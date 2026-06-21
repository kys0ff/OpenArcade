package off.kys.openarcade.data.repository

import android.app.usage.UsageStatsManager
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
import java.util.Calendar

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
        val existingGames = gameDao.getAllGamesSync()

        // Fetch usage stats
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -1)
        val stats = usageStatsManager.queryAndAggregateUsageStats(calendar.timeInMillis, System.currentTimeMillis())

        val entities = scannedGames.map { scanned ->
            val existing = existingGames.find { it.packageName == scanned.packageName }
            val usage = stats[scanned.packageName]
            
            scanned.copy(
                category = existing?.category ?: scanned.category,
                customCategories = existing?.customCategories ?: emptyList(),
                primaryColorArgb = ColorExtractor.extractPrimaryColor(scanned.icon).toArgb(),
                lastPlayed = usage?.lastTimeUsed ?: 0L,
                totalPlayTime = usage?.totalTimeInForeground ?: 0L
            )
        }
        
        // Update database without full wipe
        gameDao.insertGames(entities)
        
        // Mark games that are no longer installed as uninstalled
        val presentPackageNames = scannedGames.map { it.packageName }
        gameDao.markMissingAsUninstalled(presentPackageNames)
    }

    override suspend fun updateCustomCategories(packageName: String, customCategories: List<String>) {
        gameDao.updateCustomCategories(packageName, customCategories)
    }
}
