package off.kys.openarcade.ui.launcher.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import off.kys.openarcade.R
import off.kys.openarcade.ui.components.ArcadeDialog
import off.kys.openarcade.ui.components.ArcadeDialogButton
import off.kys.openarcade.ui.components.ArcadeDialogDefaults.ButtonRole

@Composable
fun RenameDialog(
    initialTitle: String,
    accentColor: Color,
    onConfirm: (String) -> Unit,
    onRestore: () -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }

    ArcadeDialog(
        onDismissRequest = onDismiss,
        title = stringResource(R.string.rename_dialog_title),
        content = {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.rename_dialog_label)) },
                singleLine = true,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accentColor,
                    focusedLabelColor = accentColor,
                    cursorColor = accentColor
                )
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
