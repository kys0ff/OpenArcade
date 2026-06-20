package off.kys.openarcade

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import off.kys.openarcade.domain.model.GameEntry

object GameScanner {
    fun fetchInstalledGames(context: Context): List<GameEntry> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val launchables = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            pm.queryIntentActivities(intent, 0)
        }

        val detectedGames = mutableListOf<GameEntry>()

        for (resolveInfo in launchables) {
            val appInfo = resolveInfo.activityInfo.applicationInfo

            // Check if application flags or category label it as a game
            val isGame = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                appInfo.category == ApplicationInfo.CATEGORY_GAME
            } else {
                @Suppress("DEPRECATION")
                (appInfo.flags and ApplicationInfo.FLAG_IS_GAME) != 0
            }

            if (isGame) {
                val title = appInfo.loadLabel(pm).toString()
                val icon = appInfo.loadIcon(pm)
                val packageName = appInfo.packageName

                detectedGames.add(
                    GameEntry(
                        packageName = packageName,
                        title = title,
                        category = "Installed Game",
                        primaryColorArgb = 0xFF2A2A2A.toInt(),
                        icon = icon
                    )
                )
            }
        }

        // UI fallback if user lacks mobile games
        if (detectedGames.isEmpty()) {
            return listOf(
                GameEntry(
                    packageName = context.packageName,
                    title = "System Deck (Fallback)",
                    category = "Utility",
                    primaryColorArgb = 0xFF2A2A2A.toInt(),
                    icon = context.applicationInfo.loadIcon(pm)
                )
            )
        }

        return detectedGames
    }
}