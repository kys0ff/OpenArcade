package off.kys.openarcade.data.repository

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.ui.graphics.toArgb
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import off.kys.openarcade.data.local.dao.GameDao
import off.kys.openarcade.domain.model.AnalyticsData
import off.kys.openarcade.domain.model.CategoryDistribution
import off.kys.openarcade.domain.model.GameEntry
import off.kys.openarcade.domain.model.PlayTimePoint
import off.kys.openarcade.domain.model.TopGame
import off.kys.openarcade.domain.repository.GameRepository
import off.kys.openarcade.util.ColorExtractor
import off.kys.openarcade.util.GameScanner
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

    override suspend fun refreshGames() = withContext(Dispatchers.IO) {
        val existingGames = gameDao.getAllGamesSync()
        val manuallyAdded = existingGames.filter { it.isManuallyAdded }.map { it.packageName }
        
        val scannedGames = GameScanner.fetchInstalledGames(context, manuallyAdded)

        // Fetch usage stats
        val stats = getUsageStats()

        val entities = scannedGames.map { scanned ->
            val existing = existingGames.find { it.packageName == scanned.packageName }
            val usage = stats[scanned.packageName]
            
            scanned.copy(
                category = existing?.category ?: scanned.category,
                customCategories = existing?.customCategories ?: emptyList(),
                primaryColorArgb = ColorExtractor.extractPrimaryColor(scanned.icon).toArgb(),
                lastPlayed = usage?.lastTimeUsed ?: 0L,
                totalPlayTime = usage?.totalTimeInForeground ?: 0L,
                isFavorite = existing?.isFavorite ?: false,
                customTitle = existing?.customTitle,
                customIconPath = existing?.customIconPath,
                isHidden = existing?.isHidden ?: false,
                isManuallyAdded = existing?.isManuallyAdded ?: false
            )
        }
        
        // Update database without full wipe
        gameDao.insertGames(entities)
        
        // Mark games that are no longer installed as uninstalled
        val presentPackageNames = scannedGames.map { it.packageName }
        gameDao.markMissingAsUninstalled(presentPackageNames)
    }

    override suspend fun refreshGameStats(packageName: String) = withContext(Dispatchers.IO) {
        val stats = getUsageStats()
        val usage = stats[packageName]
        if (usage != null) {
            gameDao.updatePlayStats(packageName, usage.lastTimeUsed, usage.totalTimeInForeground)
        }
    }

    override suspend fun refreshAllGameStats() = withContext(Dispatchers.IO) {
        val stats = getUsageStats()
        val existingGames = gameDao.getAllGamesSync()
        existingGames.forEach { existing ->
            val usage = stats[existing.packageName]
            if (usage != null) {
                gameDao.updatePlayStats(existing.packageName, usage.lastTimeUsed, usage.totalTimeInForeground)
            }
        }
    }

    private fun getUsageStats(): Map<String, android.app.usage.UsageStats> {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -1)
        return usageStatsManager.queryAndAggregateUsageStats(calendar.timeInMillis, System.currentTimeMillis())
    }

    override suspend fun updateCustomCategories(packageName: String, customCategories: List<String>) {
        gameDao.updateCustomCategories(packageName, customCategories)
    }

    override suspend fun updateFavoriteStatus(packageName: String, isFavorite: Boolean) {
        gameDao.updateFavoriteStatus(packageName, isFavorite)
    }

    override suspend fun updateCustomTitle(packageName: String, customTitle: String?) {
        gameDao.updateCustomTitle(packageName, customTitle)
    }

    override suspend fun updateCustomIconPath(packageName: String, customIconPath: String?) = withContext(Dispatchers.IO) {
        val finalPath = if (customIconPath?.startsWith("content://") == true) {
            saveIconToInternalStorage(packageName, customIconPath.toUri())
        } else {
            customIconPath
        }
        gameDao.updateCustomIconPath(packageName, finalPath)
    }

    private fun saveIconToInternalStorage(packageName: String, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val iconDir = File(context.filesDir, "custom_icons")
            if (!iconDir.exists()) iconDir.mkdirs()
            
            val file = File(iconDir, "${packageName}_icon.png")
            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun updateVisibility(packageName: String, isHidden: Boolean) {
        gameDao.updateVisibility(packageName, isHidden)
    }

    override suspend fun addGame(game: GameEntry) {
        gameDao.insertGame(game)
    }

    override fun getAnalyticsData(): Flow<AnalyticsData> = flow {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        // Daily Trend (Last 7 days)
        val dailyTrend = mutableListOf<PlayTimePoint>()
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        for (i in 6 downTo 0) {
            val start = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                add(Calendar.DAY_OF_YEAR, -i)
            }
            val end = Calendar.getInstance().apply {
                timeInMillis = start.timeInMillis
                add(Calendar.DAY_OF_YEAR, 1)
            }
            
            val stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start.timeInMillis, end.timeInMillis)
            val totalTime = stats.sumOf { it.totalTimeInForeground }
            dailyTrend.add(PlayTimePoint(dayFormat.format(start.time), totalTime, start.timeInMillis))
        }

        // Weekly Trend (Last 4 weeks)
        val weeklyTrend = mutableListOf<PlayTimePoint>()
        for (i in 3 downTo 0) {
            val start = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                add(Calendar.WEEK_OF_YEAR, -i)
            }
            val end = Calendar.getInstance().apply {
                timeInMillis = start.timeInMillis
                add(Calendar.WEEK_OF_YEAR, 1)
            }
            
            val stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY, start.timeInMillis, end.timeInMillis)
            val totalTime = stats.sumOf { it.totalTimeInForeground }
            weeklyTrend.add(PlayTimePoint("W${4-i}", totalTime, start.timeInMillis))
        }

        // Get all games from DB to match usage stats with categories and titles
        val games = gameDao.getAllGamesSync()
        val allStats = getUsageStats()
        
        val topGames = games.map { game ->
            val usage = allStats[game.packageName]
            TopGame(
                packageName = game.packageName,
                title = game.displayName,
                playTimeMs = usage?.totalTimeInForeground ?: 0L,
                primaryColorArgb = game.primaryColorArgb
            )
        }.filter { it.playTimeMs > 0 }.sortedByDescending { it.playTimeMs }.take(5)

        val categoryGroups = games.groupBy { it.category }
        val categoryDistribution = categoryGroups.map { (_, categoryGames) ->
            val categoryTime = categoryGames.sumOf { game ->
                allStats[game.packageName]?.totalTimeInForeground ?: 0L
            }
            categoryTime
        }.let { times ->
            val totalTime = times.sum()
            categoryGroups.keys.mapIndexed { index, category ->
                val time = times[index]
                CategoryDistribution(
                    category = category,
                    playTimeMs = time,
                    percentage = if (totalTime > 0) time.toFloat() / totalTime else 0f
                )
            }
        }.filter { it.playTimeMs > 0 }.sortedByDescending { it.playTimeMs }

        emit(AnalyticsData(
            totalPlayTimeMs = allStats.values.sumOf { it.totalTimeInForeground },
            dailyTrend = dailyTrend,
            weeklyTrend = weeklyTrend,
            categoryDistribution = categoryDistribution,
            topGames = topGames
        ))
    }
}
