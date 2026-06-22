package off.kys.openarcade.ui.app_picker.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import off.kys.openarcade.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppPickerTopBar(
    selectedCount: Int,
    isLoading: Boolean = false,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val hasSelection = selectedCount > 0 && !isLoading

    Column {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.add_to_launcher_title),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick, enabled = !isLoading) {
                    Icon(
                        painter = painterResource(R.drawable.round_arrow_back_24),
                        contentDescription = null,
                        tint = if (isLoading) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) 
                               else MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            actions = {
                val buttonBorderBrush = if (hasSelection) {
                    Brush.linearGradient(
                        listOf(
                            primary.copy(alpha = 0.85f),
                            tertiary.copy(alpha = 0.40f),
                            Color.Transparent
                        )
                    )
                } else {
                    Brush.linearGradient(
                        listOf(
                            tertiary.copy(alpha = 0.20f),
                            Color.Transparent
                        )
                    )
                }

                val buttonContainerColor by animateColorAsState(
                    targetValue = if (hasSelection)
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.72f)
                    else
                        MaterialTheme.colorScheme.surfaceContainerLow,
                    animationSpec = tween(200),
                    label = "addButtonContainer"
                )
                val buttonLabelColor by animateColorAsState(
                    targetValue = if (hasSelection)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                    animationSpec = tween(200),
                    label = "addButtonLabel"
                )

                Box(
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .height(32.dp)
                        .clip(CircleShape)
                        .background(buttonContainerColor, CircleShape)
                        .border(1.dp, buttonBorderBrush, CircleShape)
                        .clickable(enabled = hasSelection, onClick = onAddClick)
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (hasSelection) {
                            Icon(
                                painter = painterResource(R.drawable.round_check_24),
                                contentDescription = null,
                                tint = primary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = if (hasSelection)
                                stringResource(R.string.add_selected) + " ($selectedCount)"
                            else
                                stringResource(R.string.add_selected),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (hasSelection) FontWeight.SemiBold
                                else FontWeight.Medium
                            ),
                            color = buttonLabelColor
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        // 2dp accent divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            primary.copy(alpha = 0.60f),
                            tertiary.copy(alpha = 0.30f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}
