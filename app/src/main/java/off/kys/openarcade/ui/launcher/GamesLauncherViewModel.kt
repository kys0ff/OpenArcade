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
import off.kys.openarcade.domain.model.GameCategory
import off.kys.openarcade.domain.model.GameEntry
import off.kys.openarcade.domain.model.GameFilter
import off.kys.openarcade.domain.usecase.GetGamesUseCase
import off.kys.openarcade.domain.usecase.RefreshGamesUseCase
import java.io.File

class GamesLauncherViewModel(
    private val application: Application,
    private val refreshGamesUseCase: RefreshGamesUseCase,
    getGamesUseCase: GetGamesUseCase,
) : ViewModel() {

    private val selectedFilter = MutableStateFlow<GameFilter>(GameFilter.All)

    private val allGames: StateFlow<List<GameEntry>> = getGamesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
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
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = listOf(GameFilter.All)
    )

    private val batteryLevel = MutableStateFlow(0)
    private val storageUsage = MutableStateFlow(0)
    private val hasUsageStatsPermission = MutableStateFlow(true)

    val uiState: StateFlow<GamesLauncherUiState> = combine(
        combine(allGames, availableFilters, selectedFilter) { all, filters, selected ->
            Triple(all, filters, selected)
        },
        combine(batteryLevel, storageUsage, hasUsageStatsPermission) { battery, storage, permission ->
            Triple(battery, storage, permission)
        }
    ) { gameData, deviceData ->
        val (all, filters, selected) = gameData
        val (battery, storage, permission) = deviceData

        val filtered = when (selected) {
            is GameFilter.All -> all
            is GameFilter.Installed -> all.filter { it.isInstalled }
            is GameFilter.Uninstalled -> all.filter { !it.isInstalled }
            is GameFilter.System -> all.filter { it.category == selected.category }
            is GameFilter.Custom -> all.filter { selected.name in it.customCategories }
        }

        val recent = all.filter { it.lastPlayed > 0 }
            .sortedByDescending { it.lastPlayed }
            .take(5)

        GamesLauncherUiState(
            filteredGames = filtered,
            recentGames = recent,
            filters = filters,
            selectedFilter = selected,
            batteryLevel = battery,
            storageUsage = storage,
            hasUsageStatsPermission = permission
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GamesLauncherUiState()
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

    fun onEvent(event: GamesLauncherUiEvent) {
        when (event) {
            is GamesLauncherUiEvent.FilterSelected -> {
                selectedFilter.value = event.filter
            }
            is GamesLauncherUiEvent.RefreshRequested -> {
                refreshGames()
            }
            is GamesLauncherUiEvent.GameClicked -> {
                // Navigation handled in Screen, but could trigger analytics here
                Log.d("GamesLauncher", "Game clicked: ${event.packageName}")
            }
            is GamesLauncherUiEvent.GrantPermissionClicked -> {
                // Handled in Screen (startActivity)
                Log.d("GamesLauncher", "Grant permission clicked")
            }
            is GamesLauncherUiEvent.PermissionCheckRequested -> {
                updatePermissionStatus()
            }
        }
    }

    private fun refreshGames() {
        viewModelScope.launch {
            refreshGamesUseCase()
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
