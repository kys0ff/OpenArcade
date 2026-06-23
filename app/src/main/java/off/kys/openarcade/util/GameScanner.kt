package off.kys.openarcade.util

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import off.kys.openarcade.domain.model.GameCategory
import off.kys.openarcade.domain.model.GameEntry

object GameScanner {
    fun fetchInstalledGames(context: Context, extraPackages: List<String> = emptyList()): List<GameEntry> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val launcherActivities = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            pm.queryIntentActivities(intent, 0)
        }

        val detectedGames = mutableListOf<GameEntry>()

        for (resolveInfo in launcherActivities) {
            val activityInfo = resolveInfo.activityInfo
            val appInfo = activityInfo.applicationInfo
            val isGame = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                appInfo.category == ApplicationInfo.CATEGORY_GAME
            } else {
                @Suppress("DEPRECATION")
                (appInfo.flags and ApplicationInfo.FLAG_IS_GAME) != 0
            }

            if (isGame || extraPackages.contains(appInfo.packageName)) {
                val title = appInfo.loadLabel(pm).toString()
                val icon = appInfo.loadIcon(pm)
                val packageName = appInfo.packageName
                val lastUpdateTime = try {
                    pm.getPackageInfo(packageName, 0).lastUpdateTime
                } catch (_: Exception) {
                    0L
                }

                val palette = try {
                    Palette.from(icon.toBitmap()).generate()
                } catch (_: Exception) {
                    null
                }
                val primarySwatch = palette?.vibrantSwatch ?: palette?.dominantSwatch
                val secondarySwatch = palette?.mutedSwatch ?: palette?.lightVibrantSwatch
                val tertiarySwatch = palette?.darkVibrantSwatch ?: palette?.darkMutedSwatch

                val primaryColor = primarySwatch?.rgb ?: 0xFF2A2A2A.toInt()
                val onPrimaryColor = primarySwatch?.titleTextColor ?: 0xFFFFFFFF.toInt()
                val secondaryColor = secondarySwatch?.rgb ?: primaryColor
                val tertiaryColor = tertiarySwatch?.rgb ?: primaryColor

                detectedGames.add(
                    GameEntry(
                        packageName = packageName,
                        title = title,
                        category = if (isGame) GameCategory.GAME else GameCategory.UTILITY,
                        isInstalled = true,
                        primaryColorArgb = primaryColor,
                        onPrimaryColorArgb = onPrimaryColor,
                        secondaryColorArgb = secondaryColor,
                        tertiaryColorArgb = tertiaryColor,
                        lastAppUpdateTime = lastUpdateTime,
                        isManuallyAdded = !isGame
                    )
                )
            }
        }

        // UI fallback if user lacks mobile games
        if (detectedGames.isEmpty()) {
            val icon = context.applicationInfo.loadIcon(pm)
            val palette = Palette.from(icon.toBitmap()).generate()
            val primarySwatch = palette.vibrantSwatch ?: palette.dominantSwatch

            val primaryColor = primarySwatch?.rgb ?: 0xFF2A2A2A.toInt()

            return listOf(
                GameEntry(
                    packageName = context.packageName,
                    title = "System Deck (Fallback)",
                    category = GameCategory.UTILITY,
                    primaryColorArgb = primaryColor,
                    onPrimaryColorArgb = primarySwatch?.titleTextColor ?: 0xFFFFFFFF.toInt(),
                    secondaryColorArgb = palette.mutedSwatch?.rgb ?: primaryColor,
                    tertiaryColorArgb = palette.darkVibrantSwatch?.rgb ?: primaryColor
                )
            )
        }

        return detectedGames
    }
}