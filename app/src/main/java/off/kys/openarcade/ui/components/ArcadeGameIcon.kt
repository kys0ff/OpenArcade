package off.kys.openarcade.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ArcadeGameIcon(
    icon: Any?, // Can be packageName (String) or customPath (String)
    contentDescription: String?,
    modifier: Modifier = Modifier,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    tertiaryColor: Color = MaterialTheme.colorScheme.tertiary,
    iconSize: Dp = 48.dp
) {
    val context = LocalContext.current
    val imageModel = remember(icon) {
        val iconStr = icon as? String ?: return@remember icon
        if (iconStr.contains("/") || iconStr.contains("content://")) {
            // Likely a file path or URI
            iconStr
        } else {
            // Likely a package name, fetch drawable
            try {
                context.packageManager.getApplicationIcon(iconStr)
            } catch (_: Exception) {
                null
            }
        }
    }

    Box(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    listOf(
                        primaryColor.copy(alpha = 0.18f),
                        primaryColor.copy(alpha = 0.04f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Elevated icon card
        Card(
            shape = MaterialTheme.shapes.small,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            AsyncImage(
                model = imageModel,
                contentDescription = contentDescription,
                modifier = Modifier
                    .size(iconSize)
                    .padding(4.dp),
                contentScale = ContentScale.Fit
            )
        }

        // Bottom accent line
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            primaryColor.copy(alpha = 0.70f),
                            tertiaryColor.copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}
