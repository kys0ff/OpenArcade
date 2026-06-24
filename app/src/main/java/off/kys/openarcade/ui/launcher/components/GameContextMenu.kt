package off.kys.openarcade.ui.launcher.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import off.kys.openarcade.R
import off.kys.openarcade.domain.model.GameEntry
import off.kys.openarcade.ui.components.ArcadeDialog
import off.kys.openarcade.ui.components.ArcadeDialogButton
import off.kys.openarcade.ui.components.ArcadeDialogDefaults.ButtonRole
import off.kys.openarcade.ui.components.GameActionItem

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

                    if (game.isInstalled) {
                        GameActionItem(
                            text = stringResource(R.string.menu_uninstall),
                            icon = painterResource(R.drawable.round_close_24),
                            onClick = {
                                onUninstall()
                                onDismissRequest()
                            }
                        )
                    }
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

