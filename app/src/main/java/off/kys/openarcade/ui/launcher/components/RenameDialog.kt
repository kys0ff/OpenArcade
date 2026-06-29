package off.kys.openarcade.ui.launcher.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import off.kys.openarcade.R
import off.kys.openarcade.ui.components.ArcadeDialog
import off.kys.openarcade.ui.components.ArcadeDialogButton
import off.kys.openarcade.ui.components.ArcadeDialogDefaults.ButtonRole
import off.kys.openarcade.ui.components.ArcadeTextField
import off.kys.openarcade.domain.repository.MediaRepository
import org.koin.compose.koinInject

@Composable
fun RenameDialog(
    initialTitle: String,
    accentColor: Color,
    onConfirm: (String) -> Unit,
    onRestore: () -> Unit,
    onDismiss: () -> Unit,
    mediaRepository: MediaRepository = koinInject()
) {
    var title by remember { mutableStateOf(initialTitle) }
    val isDark = isSystemInDarkTheme()
    val adaptiveAccentColor = remember(accentColor, isDark) {
        mediaRepository.getAdaptiveColor(accentColor, isDark)
    }

    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = adaptiveAccentColor
        )
    ) {
        ArcadeDialog(
            onDismissRequest = onDismiss,
            title = stringResource(R.string.rename_dialog_title),
            icon = painterResource(R.drawable.round_sports_esports_24),
            iconTint = adaptiveAccentColor,
            content = {
                ArcadeTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = stringResource(R.string.rename_dialog_label),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            buttons = listOf(
                ArcadeDialogButton(
                    label = stringResource(R.string.menu_restore_default_name),
                    role = ButtonRole.Secondary,
                    onClick = {
                        onRestore()
                        onDismiss()
                    }
                ),
                ArcadeDialogButton(
                    label = stringResource(R.string.rename_dialog_save),
                    role = ButtonRole.Primary,
                    onClick = { onConfirm(title) }
                )
            )
        )
    }
}
