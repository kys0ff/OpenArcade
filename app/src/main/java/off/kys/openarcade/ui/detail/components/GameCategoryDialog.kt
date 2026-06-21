package off.kys.openarcade.ui.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import off.kys.openarcade.R
import off.kys.openarcade.ui.components.SectionHeader

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GameCategoryDialog(
    editingCategories: List<String>,
    newCategoryDraft: String,
    accentColor: Color,
    onUpdateNewCategoryDraft: (String) -> Unit,
    onAddCategory: (String) -> Unit,
    onRemoveCategory: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = MaterialTheme.shapes.extraLarge,
        icon = {
            Icon(
                painter = painterResource(R.drawable.round_category_24),
                contentDescription = null
            )
        },
        title = {
            Text(
                text = stringResource(R.string.game_detail_categories_title),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (editingCategories.isNotEmpty()) {
                    SectionHeader(
                        title = stringResource(R.string.game_detail_custom_tags_label),
                        accentColor = accentColor,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        editingCategories.forEach { category ->
                            EditableCategoryTag(
                                category = category,
                                accentColor = accentColor,
                                onRemove = { onRemoveCategory(category) }
                            )
                        }
                    }
                } else {
                    Text(
                        text = stringResource(R.string.game_detail_no_custom_tags),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = newCategoryDraft,
                    onValueChange = onUpdateNewCategoryDraft,
                    label = { Text(stringResource(R.string.game_detail_new_tag_label)) },
                    singleLine = true,
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        focusedLabelColor = accentColor,
                        cursorColor = accentColor
                    ),
                    trailingIcon = {
                        IconButton(
                            onClick = { onAddCategory(newCategoryDraft) },
                            enabled = newCategoryDraft.isNotBlank()
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.round_add_24),
                                contentDescription = stringResource(R.string.game_detail_add_tag_desc),
                                tint = if (newCategoryDraft.isNotBlank()) accentColor else MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onSave,
                colors = ButtonDefaults.textButtonColors(contentColor = accentColor)
            ) {
                Text(
                    stringResource(R.string.game_detail_save),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.game_detail_cancel))
            }
        }
    )
}

@Composable
private fun EditableCategoryTag(
    category: String,
    accentColor: Color,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                color = accentColor.copy(alpha = 0.12f),
                shape = RoundedCornerShape(6.dp)
            )
            .padding(start = 10.dp, end = 4.dp, top = 4.dp, bottom = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = category,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                color = accentColor
            )
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(18.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.round_close_24),
                    contentDescription = null,
                    tint = accentColor.copy(alpha = 0.6f),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
