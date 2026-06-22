package off.kys.openarcade.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import off.kys.openarcade.R
import off.kys.openarcade.ui.components.ArcadeDialogDefaults.ButtonRole
import off.kys.openarcade.ui.launcher.components.ArcadeFilterChip
import off.kys.openarcade.ui.theme.OpenArcadeTheme

// ─── Model ────────────────────────────────────────────────────────────────────

/**
 * Describes a single action button rendered in [ArcadeDialog].
 *
 * @param label    Button text.
 * @param role     Visual role — drives color/border treatment.
 * @param onClick  Invoked when the button is tapped.
 */
data class ArcadeDialogButton(
    val label: String,
    val role: ButtonRole = ButtonRole.Secondary,
    val onClick: () -> Unit
)

object ArcadeDialogDefaults {
    enum class ButtonRole {
        /** Primary CTA — gradient border, primaryContainer fill. */
        Primary,
        /** Neutral action — muted tertiary border, surfaceContainerLow fill. */
        Secondary,
        /** Destructive action — error color border and fill. */
        Destructive
    }
}

// ─── Dialog ───────────────────────────────────────────────────────────────────

/**
 * Arcade-styled dialog that follows the gradient-border / adaptive-color
 * language used across GameGridCard, FilterChipsRow, and AppPickerScreen.
 */
@Composable
fun ArcadeDialog(
    onDismissRequest: () -> Unit,
    title: String,
    message: String? = null,
    icon: Painter? = null,
    iconTint: Color? = null,
    buttons: List<ArcadeDialogButton> = emptyList(),
    dismissOnClickOutside: Boolean = true,
    content: (@Composable () -> Unit)? = null
) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = dismissOnClickOutside,
            dismissOnClickOutside = dismissOnClickOutside,
            usePlatformDefaultWidth = false
        )
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(tween(180)) + scaleIn(tween(220), initialScale = 0.92f),
            exit = fadeOut(tween(150)) + scaleOut(tween(150), targetScale = 0.94f)
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            listOf(
                                tertiary.copy(alpha = 0.55f),
                                primary.copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        ),
                        shape = MaterialTheme.shapes.large
                    )
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {

                    // ── Header accent bar ─────────────────────────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        primary.copy(alpha = 0.75f),
                                        tertiary.copy(alpha = 0.40f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // ── Optional icon ─────────────────────────────────────
                        if (icon != null) {
                            val resolvedTint = iconTint ?: primary

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(
                                                resolvedTint.copy(alpha = 0.18f),
                                                resolvedTint.copy(alpha = 0.04f)
                                            )
                                        )
                                    )
                                    .border(
                                        1.dp,
                                        Brush.linearGradient(
                                            listOf(
                                                resolvedTint.copy(alpha = 0.55f),
                                                Color.Transparent
                                            )
                                        ),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = icon,
                                    contentDescription = null,
                                    tint = resolvedTint,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(Modifier.height(14.dp))
                        }

                        // ── Title ─────────────────────────────────────────────
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        // ── Message ───────────────────────────────────────────
                        if (message != null) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }

                        // ── Custom content slot ───────────────────────────────
                        if (content != null) {
                            Spacer(Modifier.height(16.dp))
                            content()
                        }

                        // ── Buttons ───────────────────────────────────────────
                        if (buttons.isNotEmpty()) {
                            Spacer(Modifier.height(24.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(
                                    space = 8.dp,
                                    alignment = Alignment.End
                                )
                            ) {
                                buttons.forEach { button ->
                                    ArcadeDialogActionButton(button = button)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── Action button ────────────────────────────────────────────────────────────

@Composable
private fun ArcadeDialogActionButton(button: ArcadeDialogButton) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val error = MaterialTheme.colorScheme.error

    val (containerColor, labelColor, borderStart, borderEnd) = when (button.role) {
        ButtonRole.Primary -> ArcadeButtonColors(
            container = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.72f),
            label = MaterialTheme.colorScheme.onPrimaryContainer,
            borderStart = primary.copy(alpha = 0.85f),
            borderEnd = tertiary.copy(alpha = 0.40f)
        )
        ButtonRole.Secondary -> ArcadeButtonColors(
            container = MaterialTheme.colorScheme.surfaceContainerLow,
            label = MaterialTheme.colorScheme.onSurfaceVariant,
            borderStart = tertiary.copy(alpha = 0.30f),
            borderEnd = Color.Transparent
        )
        ButtonRole.Destructive -> ArcadeButtonColors(
            container = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.55f),
            label = MaterialTheme.colorScheme.onErrorContainer,
            borderStart = error.copy(alpha = 0.80f),
            borderEnd = error.copy(alpha = 0.25f)
        )
    }

    Box(
        modifier = Modifier
            .height(34.dp)
            .clip(CircleShape)
            .background(containerColor, CircleShape)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(listOf(borderStart, borderEnd)),
                shape = CircleShape
            )
            .clickable(
                onClick = button.onClick,
                indication = ripple(bounded = true),
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = button.label,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = when (button.role) {
                    ButtonRole.Primary, ButtonRole.Destructive -> FontWeight.SemiBold
                    ButtonRole.Secondary -> FontWeight.Medium
                }
            ),
            color = labelColor
        )
    }
}

// ─── Internal token carrier ───────────────────────────────────────────────────

private data class ArcadeButtonColors(
    val container: Color,
    val label: Color,
    val borderStart: Color,
    val borderEnd: Color
)

// ─── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun ArcadeDialogPreview() {
    OpenArcadeTheme {
        var show by remember { mutableStateOf(true) }

        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (show) {
                ArcadeDialog(
                    onDismissRequest = { show = false },
                    title = "Remove game?",
                    message = "This will remove the game from your arcade library. You can always add it back later.",
                    icon = painterResource(R.drawable.round_check_24),
                    buttons = listOf(
                        ArcadeDialogButton(
                            label = "Cancel",
                            role = ButtonRole.Secondary,
                            onClick = { show = false }
                        ),
                        ArcadeDialogButton(
                            label = "Remove",
                            role = ButtonRole.Destructive,
                            onClick = { show = false }
                        )
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ArcadeDialogWithContentPreview() {
    OpenArcadeTheme {
        var show by remember { mutableStateOf(true) }

        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (show) {
                ArcadeDialog(
                    onDismissRequest = { show = false },
                    title = "Sort games",
                    icon = painterResource(R.drawable.round_explore_24),
                    content = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("A → Z", "Last played", "Most played").forEachIndexed { i, label ->
                                ArcadeFilterChip(
                                    label = label,
                                    selected = i == 0,
                                    onClick = {}
                                )
                            }
                        }
                    },
                    buttons = listOf(
                        ArcadeDialogButton("Cancel", ButtonRole.Secondary) { show = false },
                        ArcadeDialogButton("Apply", ButtonRole.Primary) { show = false }
                    )
                )
            }
        }
    }
}