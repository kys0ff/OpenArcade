package off.kys.openarcade.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.graphics.drawable.toBitmap
import java.io.File
import java.io.FileOutputStream

class IconManager(private val context: Context) {
    companion object {
        private const val CUSTOM_ICONS_DIR = "custom_icons"
        private const val CACHED_ICONS_DIR = "cached_icons"
    }

    fun saveCustomIcon(packageName: String, uri: Uri): String? {
        return try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri)
            val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "png"
            
            val inputStream = contentResolver.openInputStream(uri)
            val iconDir = File(context.filesDir, CUSTOM_ICONS_DIR)
            if (!iconDir.exists()) iconDir.mkdirs()

            val fileName = "${packageName}_custom.$extension"
            val file = File(iconDir, fileName)
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

    fun saveExtractedIcon(packageName: String, drawable: Drawable): String? {
        return try {
            val bitmap = drawable.toBitmap()
            val iconDir = File(context.filesDir, CACHED_ICONS_DIR)
            if (!iconDir.exists()) iconDir.mkdirs()

            val file = File(iconDir, "${packageName}_cached.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
