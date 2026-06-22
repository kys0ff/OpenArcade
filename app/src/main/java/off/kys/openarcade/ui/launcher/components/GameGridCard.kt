package off.kys.openarcade.ui.launcher.components

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.text.format.DateUtils
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import off.kys.openarcade.R
import off.kys.openarcade.domain.model.GameEntry
import off.kys.openarcade.ui.launcher.GamesLauncherUiEvent
import off.kys.openarcade.util.ColorExtractor

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GameGridCard(
    game: GameEntry,
    onClick: () -> Unit,
    onEvent: (GamesLauncherUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val adaptivePrimary = ColorExtractor.getAdaptiveColor(game.getPrimaryColor(), isDark)
    val adaptiveTertiary = ColorExtractor.getAdaptiveColor(game.getTertiaryColor(), isDark)

    var showMenu by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }

    val iconPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            onEvent(GamesLauncherUiEvent.ChangeIconRequested(game.packageName, it.toString()))
        }
    }

    if (showRenameDialog) {
        RenameDialog(
            initialTitle = game.displayName,
            accentColor = adaptivePrimary,
            onConfirm = { 
                onEvent(GamesLauncherUiEvent.RenameRequested(game.packageName, it))
                showRenameDialog = false
            },
            onRestore = {
                onEvent(GamesLauncherUiEvent.RenameRequested(game.packageName, null))
                showRenameDialog = false
            },
            onDismiss = { showRenameDialog = false }
        )
    }

    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(
                    if (game.isFavorite) adaptivePrimary.copy(alpha = 0.8f) else adaptiveTertiary.copy(alpha = 0.45f),
                    Color.Transparent
                )
            ),
            width = if (game.isFavorite) 2.dp else 1.dp
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = { showMenu = true }
            )
    ) {
        Box {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.25f)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    adaptivePrimary.copy(alpha = 0.18f),
                                    adaptivePrimary.copy(alpha = 0.04f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = MaterialTheme.shapes.small,
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        AsyncImage(
                            model = game.customIconPath ?: game.icon,
                            contentDescription = null,
                            modifier = Modifier
                                .size(52.dp)
                                .padding(4.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    adaptivePrimary.copy(alpha = 0.70f),
                                    adaptiveTertiary.copy(alpha = 0.35f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = game.displayName,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        if (game.isFavorite) {
                            Icon(
                                painter = painterResource(R.drawable.round_favorite_24),
                                contentDescription = null,
                                tint = adaptivePrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(2.dp))
                    val lastPlayedText = if (game.lastPlayed > 0) {
                        DateUtils.getRelativeTimeSpanString(
                            game.lastPlayed,
                            System.currentTimeMillis(),
                            DateUtils.MINUTE_IN_MILLIS
                        ).toString()
                    } else {
                        stringResource(R.string.never_played)
                    }

                    Text(
                        text = lastPlayedText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            GameContextMenu(
                game = game,
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                onFavoriteToggle = { 
                    onEvent(GamesLauncherUiEvent.FavoriteToggled(game.packageName, !game.isFavorite))
                },
                onRename = { showRenameDialog = true },
                onRemove = { 
                    onEvent(GamesLauncherUiEvent.VisibilityToggled(game.packageName, !game.isHidden))
                },
                onAppInfo = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", game.packageName, null)
                    }
                    context.startActivity(intent)
                },
                onUninstall = {
                    val intent = Intent(Intent.ACTION_DELETE).apply {
                        data = Uri.fromParts("package", game.packageName, null)
                    }
                    context.startActivity(intent)
                },
                onChangeIcon = {
                    iconPickerLauncher.launch("image/*")
                },
                onRestoreDefaultName = {
                    onEvent(GamesLauncherUiEvent.RenameRequested(game.packageName, null))
                },
                onRestoreDefaultIcon = {
                    onEvent(GamesLauncherUiEvent.ChangeIconRequested(game.packageName, null))
                }
            )
        }
    }
}
