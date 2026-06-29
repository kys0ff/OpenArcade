package off.kys.openarcade.data.repository

import android.app.usage.UsageStatsManager
import android.content.Context
import androidx.compose.ui.graphics.toArgb
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import off.kys.openarcade.data.local.dao.GameDao
import off.kys.openarcade.domain.model.AnalyticsData
import off.kys.openarcade.domain.model.CategoryDistribution
import off.kys.openarcade.domain.model.GameCategory
import off.kys.openarcade.domain.model.GameEntry
import off.kys.openarcade.domain.model.PlayTimePoint
import off.kys.openarcade.domain.model.TopGame
import off.kys.openarcade.domain.repository.GameRepository
import off.kys.openarcade.domain.repository.MediaRepository
import off.kys.openarcade.domain.repository.SystemRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class GameRepositoryImpl(
    private val context: Context,
    private val gameDao: GameDao,
    private val mediaRepository: MediaRepository,
    private val systemRepository: SystemRepository
) : GameRepository {

    override fun getGames(): Flow<List<GameEntry>> {
        return gameDao.getAllGames()
    }

    override fun getGameByPackageName(packageName: String): Flow<GameEntry?> {
        return gameDao.getGameByPackageName(packageName)
    }

    override suspend fun refreshGames() = withContext(Dispatchers.IO) {
        val existingGames = gameDao.getAllGamesSync()
        val manuallyAdded = existingGames.filter { it.isManuallyAdded }.map { it.packageName }
        
        val scannedGames = systemRepository.fetchInstalledGames(manuallyAdded)

        // Fetch usage stats
        val stats = getUsageStats()

        val entities = scannedGames.map { scanned ->
            val existing = existingGames.find { it.packageName == scanned.packageName }
            val usage = stats[scanned.packageName]
            
            val icon = try {
                context.packageManager.getApplicationIcon(scanned.packageName)
            } catch (_: Exception) {
                null
            }

            val cachedIconPath = if (existing == null || existing.lastAppUpdateTime != scanned.lastAppUpdateTime || existing.cachedIconPath == null) {
                icon?.let { mediaRepository.saveExtractedIcon(scanned.packageName, it) }
            } else {
                existing.cachedIconPath
            }

            scanned.copy(
                category = if (existing == null || existing.category == GameCategory.UNDEFINED) scanned.category else existing.category,
                customCategories = existing?.customCategories ?: emptyList(),
                primaryColorArgb = mediaRepository.extractPrimaryColor(icon).toArgb(),
                lastPlayed = usage?.lastTimeUsed ?: 0L,
                totalPlayTime = usage?.totalTimeInForeground ?: 0L,
                isFavorite = existing?.isFavorite ?: false,
                customTitle = existing?.customTitle,
                customIconPath = existing?.customIconPath,
                cachedIconPath = cachedIconPath,
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

    override suspend fun updateCustomCategories(packageName: String, customCategories: List<String>) = withContext(Dispatchers.IO) {
        gameDao.updateCustomCategories(packageName, customCategories)
    }

    override suspend fun updateFavoriteStatus(packageName: String, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        gameDao.updateFavoriteStatus(packageName, isFavorite)
    }

    override suspend fun updateCustomTitle(packageName: String, customTitle: String?) = withContext(Dispatchers.IO) {
        gameDao.updateCustomTitle(packageName, customTitle)
    }

    override suspend fun updateCustomIconPath(packageName: String, customIconPath: String?) = withContext(Dispatchers.IO) {
        val finalPath = if (customIconPath?.startsWith("content://") == true) {
            mediaRepository.saveCustomIcon(packageName, customIconPath.toUri())
        } else {
            customIconPath
        }
        gameDao.updateCustomIconPath(packageName, finalPath)
    }

    override suspend fun updateVisibility(packageName: String, isHidden: Boolean) = withContext(Dispatchers.IO) {
        gameDao.updateVisibility(packageName, isHidden)
    }

    override suspend fun addGame(game: GameEntry) = withContext(Dispatchers.IO) {
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
        val categoryDistribution = categoryGroups.map { (category, categoryGames) ->
            val categoryTime = categoryGames.sumOf { game ->
                allStats[game.packageName]?.totalTimeInForeground ?: 0L
            }
            category to categoryTime
        }.let { pairs ->
            val totalTime = pairs.sumOf { it.second }
            pairs.map { (category, time) ->
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
    }.flowOn(Dispatchers.Default)
}
