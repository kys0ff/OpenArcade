package off.kys.openarcade.util

import android.content.Context
import android.text.format.DateUtils
import off.kys.openarcade.R
import java.util.concurrent.TimeUnit

class TimeUtils(private val context: Context) {
    fun formatLastPlayed(lastPlayed: Long): String {
        if (lastPlayed == 0L) return context.getString(R.string.never_played)
        return DateUtils.getRelativeTimeSpanString(
            lastPlayed,
            System.currentTimeMillis(),
            DateUtils.MINUTE_IN_MILLIS
        ).toString()
    }

    fun formatTotalPlayTime(totalPlayTime: Long): String {
        if (totalPlayTime == 0L) return context.getString(R.string.no_play_time)
        
        val hours = TimeUnit.MILLISECONDS.toHours(totalPlayTime)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(totalPlayTime) % 60
        
        return when {
            hours > 0 -> context.getString(R.string.play_time_hours_minutes, hours, minutes)
            minutes > 0 -> context.getString(R.string.play_time_minutes, minutes)
            else -> context.getString(R.string.play_time_seconds, TimeUnit.MILLISECONDS.toSeconds(totalPlayTime))
        }
    }
}
