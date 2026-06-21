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
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    val allGames: StateFlow<List<GameEntry>> = getGamesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val availableFilters: StateFlow<List<GameFilter>> = allGames.map { games ->
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

    private val _batteryLevel = MutableStateFlow(0)
    val batteryLevel: StateFlow<Int> = _batteryLevel.asStateFlow()

    private val _storageInfo = MutableStateFlow(0) // Percentage used
    val storageUsage: StateFlow<Int> = _storageInfo.asStateFlow()

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            if (level != -1 && scale != -1) {
                _batteryLevel.value = (level * 100 / scale.toFloat()).toInt()
            }
        }
    }

    init {
        refreshGames()
        updateStorageInfo()
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        application.registerReceiver(batteryReceiver, filter)
    }

    fun refreshGames() {
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
            _storageInfo.value = (usedBytes.toFloat() / totalBytes * 100).toInt()
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
            _storageInfo.value = (usedBytes.toFloat() / totalBytes * 100).toInt()
        } catch (_: Exception) {
            _storageInfo.value = 0
        }
    }

    fun hasUsageStatsPermission(): Boolean {
        val appOps = application.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            application.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }
}
