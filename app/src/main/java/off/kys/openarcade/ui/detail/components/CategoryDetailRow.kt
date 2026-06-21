package off.kys.openarcade.ui.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import off.kys.openarcade.R

/**
 * Custom Detail Row built specifically to house responsive tags inside FlowRow.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryDetailRow(
    label: String,
    categories: List<String>,
    accentColor: Color,
    isFirst: Boolean,
    isLast: Boolean,
    onClick: () -> Unit
) {
    val topRadius = if (isFirst) 12.dp else 4.dp
    val bottomRadius = if (isLast) 12.dp else 4.dp
    val shape = RoundedCornerShape(
        topStart = topRadius, topEnd = topRadius,
        bottomStart = bottomRadius, bottomEnd = bottomRadius
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top // Align Top to handle wrapping gracefully
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp) // align with text labels inside row
            )

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Top
            ) {
                FlowRow(
                    modifier = Modifier.widthIn(max = 220.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.End),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { category ->
                        Box(
                            modifier = Modifier
                                .background(
                                    color = accentColor.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                                color = accentColor
                            )
                        }
                    }
                }

                Spacer(Modifier.width(6.dp))

                Icon(
                    painter = painterResource(R.drawable.round_chevron_left_24),
                    contentDescription = null,
                    tint = accentColor.copy(alpha = 0.45f),
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .size(15.dp)
                        .graphicsLayer { scaleX = -1f }
                )
            }
        }
    }

    if (!isLast) {
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            thickness = 0.5.dp
        )
    }
}
