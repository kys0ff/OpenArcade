package off.kys.openarcade.ui.launcher

import android.app.AppOpsManager
import android.app.Application
import android.app.usage.StorageStatsManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Process
import android.os.StatFs
import android.os.storage.StorageManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import off.kys.openarcade.data.local.ArcadePreferences
import off.kys.openarcade.domain.model.GameCategory
import off.kys.openarcade.domain.model.GameEntry
import off.kys.openarcade.domain.model.GameFilter
import off.kys.openarcade.domain.model.GameSortOption
import off.kys.openarcade.domain.repository.GameRepository
import off.kys.openarcade.domain.usecase.GetGamesUseCase
import off.kys.openarcade.domain.usecase.RefreshAllGameStatsUseCase
import off.kys.openarcade.domain.usecase.RefreshGamesUseCase
import java.io.File

class GamesLauncherViewModel(
    private val application: Application,
    private val refreshGamesUseCase: RefreshGamesUseCase,
    private val refreshAllGameStatsUseCase: RefreshAllGameStatsUseCase,
    private val gameRepository: GameRepository,
    private val prefs: ArcadePreferences,
    getGamesUseCase: GetGamesUseCase
) : ViewModel() {

    private val selectedFilter = MutableStateFlow<GameFilter>(GameFilter.All)

    private val allGames: StateFlow<List<GameEntry>> = getGamesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    private val availableFilters: StateFlow<List<GameFilter>> = allGames.map { games ->
        val filters = mutableListOf<GameFilter>()
        if (games.isNotEmpty()) {
            filters.add(GameFilter.All)
            filters.add(GameFilter.Installed)
            if (games.any { !it.isInstalled }) {
                filters.add(GameFilter.Uninstalled)
            }
            if (games.any { it.isHidden }) {
                filters.add(GameFilter.Hidden)
            }
        }

        GameCategory.entries.forEach { category ->
            if (category != GameCategory.UNDEFINED && games.any { it.category == category }) {
                filters.add(GameFilter.System(category))
            }
        }

        val custom = games.flatMap { it.customCategories }.distinct().sorted()
        custom.forEach { filters.add(GameFilter.Custom(it)) }

        filters
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = listOf(GameFilter.All)
    )

    private val batteryLevel = MutableStateFlow(0)
    private val storageUsage = MutableStateFlow(0)
    private val hasUsageStatsPermission = MutableStateFlow(true)
    private val isLoading = MutableStateFlow(true)

    val uiState: StateFlow<GamesLauncherUiState> = combine(
        combine(allGames, availableFilters, selectedFilter, prefs.selectedSort) { all, filters, selected, sort ->
            DataBundle(all, filters, selected, sort)
        },
        combine(
            batteryLevel,
            storageUsage,
            hasUsageStatsPermission,
            isLoading
        ) { battery, storage, permission, loading ->
            LoadingData(battery, storage, permission, loading)
        }
    ) { gameData, deviceData ->
        val (all, filters, selected, sort) = gameData
        val (battery, storage, permission, loading) = deviceData
        val visibleGames = all.filter { !it.isHidden }

        val filtered = when (selected) {
            is GameFilter.All -> visibleGames
            is GameFilter.Installed -> visibleGames.filter { it.isInstalled }
            is GameFilter.Uninstalled -> visibleGames.filter { !it.isInstalled }
            is GameFilter.System -> visibleGames.filter { it.category == selected.category }
            is GameFilter.Custom -> visibleGames.filter { selected.name in it.customCategories }
            is GameFilter.Hidden -> all.filter { it.isHidden }
        }

        val sorted = when (sort) {
            GameSortOption.TITLE_ASC -> filtered.sortedBy { it.displayName.lowercase() }
            GameSortOption.TITLE_DESC -> filtered.sortedByDescending { it.displayName.lowercase() }
            GameSortOption.LAST_PLAYED -> filtered.sortedByDescending { it.lastPlayed }
            GameSortOption.TOTAL_PLAY_TIME -> filtered.sortedByDescending { it.totalPlayTime }
        }

        val favorites = all.filter { it.isFavorite }
            .sortedBy { it.displayName.lowercase() }

        val recent = all.filter { it.lastPlayed > 0 }
            .sortedByDescending { it.lastPlayed }
            .take(5)

        GamesLauncherUiState(
            filteredGames = sorted,
            favoriteGames = favorites,
            recentGames = recent,
            filters = filters,
            selectedFilter = selected,
            selectedSort = sort,
            batteryLevel = battery,
            storageUsage = storage,
            hasUsageStatsPermission = permission,
            isLoading = loading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = GamesLauncherUiState()
    )

    private data class DataBundle(
        val all: List<GameEntry>,
        val filters: List<GameFilter>,
        val selected: GameFilter,
        val sort: GameSortOption
    )

    private data class LoadingData(
        val battery: Int,
        val storage: Int,
        val permission: Boolean,
        val loading: Boolean
    )

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            if (level != -1 && scale != -1) {
                batteryLevel.value = (level * 100 / scale.toFloat()).toInt()
            }
        }
    }

    init {
        refreshGames()
        updateStorageInfo()
        updatePermissionStatus()
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        application.registerReceiver(batteryReceiver, filter)
    }

    override fun onCleared() {
        application.unregisterReceiver(batteryReceiver)
    }

    fun onEvent(event: GamesLauncherUiEvent) = when (event) {
        is GamesLauncherUiEvent.FilterSelected -> {
            selectedFilter.value = event.filter
        }

        is GamesLauncherUiEvent.SortSelected -> {
            prefs.setSortOption(event.sort)
        }

        is GamesLauncherUiEvent.RefreshRequested -> refreshGames()
        is GamesLauncherUiEvent.GameClicked -> Log.d(
            "GamesLauncher",
            "Game clicked: ${event.packageName}"
        )

        is GamesLauncherUiEvent.GrantPermissionClicked -> Log.d(
            "GamesLauncher",
            "Grant permission clicked"
        )

        is GamesLauncherUiEvent.PermissionCheckRequested -> updatePermissionStatus()
        is GamesLauncherUiEvent.RefreshStats -> refreshStats()

        is GamesLauncherUiEvent.FavoriteToggled -> {
            viewModelScope.launch {
                gameRepository.updateFavoriteStatus(event.packageName, event.isFavorite)
            }
        }

        is GamesLauncherUiEvent.VisibilityToggled -> {
            viewModelScope.launch {
                gameRepository.updateVisibility(event.packageName, event.isHidden)
            }
        }

        is GamesLauncherUiEvent.RenameRequested -> {
            viewModelScope.launch {
                gameRepository.updateCustomTitle(event.packageName, event.newTitle?.takeIf { it.isNotBlank() })
            }
        }

        is GamesLauncherUiEvent.ChangeIconRequested -> {
            viewModelScope.launch {
                gameRepository.updateCustomIconPath(event.packageName, event.newIconPath)
            }
        }

        is GamesLauncherUiEvent.AppInfoRequested -> {
            // Handled by UI via intent
        }

        is GamesLauncherUiEvent.UninstallRequested -> {
            // Handled by UI via intent
        }

        is GamesLauncherUiEvent.AddGamesRequested -> {
            viewModelScope.launch {
                event.packageNames.forEach { pkg ->
                    gameRepository.addGame(
                        GameEntry(
                            packageName = pkg,
                            title = "",
                            category = GameCategory.UTILITY,
                            primaryColorArgb = 0,
                            onPrimaryColorArgb = 0,
                            secondaryColorArgb = 0,
                            tertiaryColorArgb = 0,
                            isManuallyAdded = true
                        )
                    )
                }
                refreshGames()
            }
        }
    }

    private fun refreshGames() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                refreshGamesUseCase()
            } finally {
                isLoading.value = false
            }
        }
    }

    private fun refreshStats() {
        viewModelScope.launch {
            refreshAllGameStatsUseCase()
        }
    }

    private fun updateStorageInfo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            updateStorageInfoOreo()
        } else {
            updateStorageInfoLegacy()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateStorageInfoOreo() {
        try {
            val storageStatsManager =
                application.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
            val totalBytes = storageStatsManager.getTotalBytes(StorageManager.UUID_DEFAULT)
            val freeBytes = storageStatsManager.getFreeBytes(StorageManager.UUID_DEFAULT)
            val usedBytes = totalBytes - freeBytes
            storageUsage.value = (usedBytes.toFloat() / totalBytes * 100).toInt()
        } catch (_: Exception) {
            updateStorageInfoLegacy()
        }
    }

    private fun updateStorageInfoLegacy() {
        try {
            val stat = StatFs(File("/data").path)
            val totalBytes = stat.blockCountLong * stat.blockSizeLong
            val availableBytes = stat.availableBlocksLong * stat.blockSizeLong
            val usedBytes = totalBytes - availableBytes
            storageUsage.value = (usedBytes.toFloat() / totalBytes * 100).toInt()
        } catch (_: Exception) {
            storageUsage.value = 0
        }
    }

    private fun updatePermissionStatus() {
        val wasGranted = hasUsageStatsPermission.value
        val isNowGranted = hasUsageStatsPermission()
        hasUsageStatsPermission.value = isNowGranted

        if (!wasGranted && isNowGranted) {
            refreshGames()
            Log.d("GamesLauncher", "Usage stats permission granted - refreshing games")
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOps = application.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            application.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }
}
