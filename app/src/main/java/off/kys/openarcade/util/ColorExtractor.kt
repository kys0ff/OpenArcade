package off.kys.openarcade.util

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.createBitmap
import androidx.palette.graphics.Palette

object ColorExtractor {
    fun extractPrimaryColor(drawable: Drawable?): Color {
        if (drawable == null) return Color(0xFF2A2A2A)
        
        val bitmap = try {
            val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 100
            val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 100
            val bitmap = createBitmap(width, height)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (_: Exception) {
            null
        } ?: return Color(0xFF2A2A2A)

        val palette = Palette.from(bitmap).generate()
        
        // Try to get dominant, then vibrant, then muted, then fallback
        val colorInt = palette.getDominantColor(
            palette.getVibrantColor(
                palette.getMutedColor(Color(0xFF2A2A2A).toArgb())
            )
        )
        
        return Color(colorInt)
    }

    fun getAdaptiveColor(color: Color, isDark: Boolean): Color {
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(color.toArgb(), hsl)

        if (isDark) {
            // In dark theme, if the color is too dark, lighten it
            if (hsl[2] < 0.65f) {
                hsl[2] = 0.65f
            }
        } else {
            // In light theme, if the color is too light, darken it
            if (hsl[2] > 0.45f) {
                hsl[2] = 0.45f
            }
        }

        return Color(ColorUtils.HSLToColor(hsl))
    }
}
