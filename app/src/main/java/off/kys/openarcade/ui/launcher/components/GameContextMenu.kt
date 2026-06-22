package off.kys.openarcade.ui.launcher.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import off.kys.openarcade.R
import off.kys.openarcade.domain.model.GameEntry
import off.kys.openarcade.ui.components.ArcadeDialog
import off.kys.openarcade.ui.components.ArcadeDialogButton
import off.kys.openarcade.ui.components.ArcadeDialogDefaults.ButtonRole

@Composable
fun GameContextMenu(
    game: GameEntry,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onRename: () -> Unit,
    onRemove: () -> Unit,
    onAppInfo: () -> Unit,
    onUninstall: () -> Unit,
    onChangeIcon: () -> Unit,
    onRestoreDefaultName: () -> Unit,
    onRestoreDefaultIcon: () -> Unit
) {
    if (expanded) {
        ArcadeDialog(
            onDismissRequest = onDismissRequest,
            title = game.displayName,
            icon = painterResource(R.drawable.round_sports_esports_24),
            content = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    GameActionItem(
                        text = stringResource(if (game.isFavorite) R.string.menu_unpin_from_favorites else R.string.menu_pin_to_favorites),
                        icon = painterResource(if (game.isFavorite) R.drawable.round_favorite_24 else R.drawable.round_favorite_border_24),
                        onClick = {
                            onFavoriteToggle()
                            onDismissRequest()
                        }
                    )

                    GameActionItem(
                        text = stringResource(R.string.menu_rename),
                        icon = painterResource(R.drawable.round_check_24),
                        onClick = {
                            onRename()
                            onDismissRequest()
                        }
                    )

                    if (game.customTitle != null) {
                        GameActionItem(
                            text = stringResource(R.string.menu_restore_default_name),
                            icon = painterResource(R.drawable.round_history_24),
                            onClick = {
                                onRestoreDefaultName()
                                onDismissRequest()
                            }
                        )
                    }

                    GameActionItem(
                        text = stringResource(R.string.menu_change_icon),
                        icon = painterResource(R.drawable.round_category_24),
                        onClick = {
                            onChangeIcon()
                            onDismissRequest()
                        }
                    )

                    if (game.customIconPath != null) {
                        GameActionItem(
                            text = stringResource(R.string.menu_restore_default_icon),
                            icon = painterResource(R.drawable.round_history_24),
                            onClick = {
                                onRestoreDefaultIcon()
                                onDismissRequest()
                            }
                        )
                    }

                    GameActionItem(
                        text = stringResource(if (game.isHidden) R.string.menu_restore_to_collection else R.string.menu_remove_from_collection),
                        icon = painterResource(R.drawable.round_close_24),
                        onClick = {
                            onRemove()
                            onDismissRequest()
                        }
                    )

                    GameActionItem(
                        text = stringResource(R.string.menu_app_info),
                        icon = painterResource(R.drawable.round_explore_24),
                        onClick = {
                            onAppInfo()
                            onDismissRequest()
                        }
                    )

                    GameActionItem(
                        text = stringResource(R.string.menu_uninstall),
                        icon = painterResource(R.drawable.round_close_24),
                        onClick = {
                            onUninstall()
                            onDismissRequest()
                        }
                    )
                }
            },
            buttons = listOf(
                ArcadeDialogButton(
                    label = stringResource(R.string.game_detail_cancel),
                    role = ButtonRole.Secondary,
                    onClick = onDismissRequest
                )
            )
        )
    }
}

@Composable
private fun GameActionItem(
    text: String,
    icon: Painter,
    onClick: () -> Unit,
    role: GameActionItemRole = GameActionItemRole.Default
) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val error = MaterialTheme.colorScheme.error

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val (accentColor, containerColor, labelColor) = when (role) {
        GameActionItemRole.Default -> Triple(
            primary,
            primary.copy(alpha = if (isPressed) 0.14f else 0.0f),
            MaterialTheme.colorScheme.onSurface
        )

        GameActionItemRole.Destructive -> Triple(
            error,
            error.copy(alpha = if (isPressed) 0.12f else 0.0f),
            MaterialTheme.colorScheme.onSurface
        )
    }

    val animatedContainer by animateColorAsState(
        targetValue = containerColor,
        animationSpec = tween(180),
        label = "actionItemContainer"
    )

    val borderBrush = Brush.linearGradient(
        listOf(
            accentColor.copy(alpha = if (isPressed) 0.55f else 0.18f),
            tertiary.copy(alpha = if (isPressed) 0.30f else 0.08f),
            Color.Transparent
        )
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(animatedContainer, MaterialTheme.shapes.medium)
            .border(1.dp, borderBrush, MaterialTheme.shapes.medium)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(MaterialTheme.shapes.small)
                .background(
                    Brush.radialGradient(
                        listOf(
                            accentColor.copy(alpha = 0.18f),
                            accentColor.copy(alpha = 0.04f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            accentColor.copy(alpha = 0.45f),
                            Color.Transparent
                        )
                    ),
                    shape = MaterialTheme.shapes.small
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(18.dp)
            )
        }

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = labelColor,
            modifier = Modifier.weight(1f)
        )

        Icon(
            painter = painterResource(R.drawable.round_arrow_back_24),
            contentDescription = null,
            tint = accentColor.copy(alpha = 0.35f),
            modifier = Modifier
                .size(14.dp)
                .graphicsLayer { rotationZ = 180f }
        )
    }
}

private enum class GameActionItemRole { Default, Destructive }