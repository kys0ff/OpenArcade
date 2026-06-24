package off.kys.openarcade.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import off.kys.openarcade.R
import off.kys.openarcade.ui.theme.OpenArcadeTheme

private val ButtonHeightDefault = 48.dp
private val ButtonHeightSmall   = 36.dp
private val ButtonHeightLarge   = 56.dp

private val ButtonHPadDefault   = 20.dp
private val ButtonHPadSmall     = 14.dp
private val ButtonHPadLarge     = 28.dp

private val ButtonIconSize      = 18.dp
private val ButtonIconSpacing   = 8.dp

private const val AnimDuration  = 200
private const val PressScale    = 0.97f

enum class ArcadeButtonRole {
    /** Primary CTA — gradient border, primaryContainer fill. */
    Primary,
    /** Neutral action — muted tertiary border, surfaceContainerLow fill. */
    Secondary,
    /** Ghost — transparent fill, gradient border only. */
    Ghost,
    /** Destructive — error color border and fill. */
    Destructive
}

enum class ArcadeButtonSize {
    Small, Default, Large
}

/**
 * Arcade-styled button following the gradient-border / press-feedback language
 * used across ArcadeFilterChip, ArcadeDialog, and GameActionItem.
 *
 * @param onClick    Invoked on tap.
 * @param label      Button text.
 * @param modifier   Applied to the outer Box.
 * @param role       Visual role — drives color/border treatment.
 * @param size       Height and horizontal padding tier.
 * @param enabled    When false the button is dimmed and non-interactive.
 * @param leadingIcon Optional icon drawn before the label.
 * @param trailingIcon Optional icon drawn after the label.
 * @param fullWidth  When true the button fills available width.
 */
@Composable
fun ArcadeButton(
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    role: ArcadeButtonRole = ArcadeButtonRole.Primary,
    size: ArcadeButtonSize = ArcadeButtonSize.Default,
    enabled: Boolean = true,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    fullWidth: Boolean = false
) {
    val primary   = MaterialTheme.colorScheme.primary
    val tertiary  = MaterialTheme.colorScheme.tertiary
    val error     = MaterialTheme.colorScheme.error

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val effectivePressed = isPressed && enabled

    // ── Resolve role tokens ───────────────────────────────────────────────────

    val targetContainer = when {
        !enabled -> MaterialTheme.colorScheme.surfaceContainerLow
        role == ArcadeButtonRole.Primary -> MaterialTheme.colorScheme.primaryContainer
            .copy(alpha = if (effectivePressed) 0.92f else 0.72f)
        role == ArcadeButtonRole.Secondary -> MaterialTheme.colorScheme.surfaceContainerLow
        role == ArcadeButtonRole.Ghost -> primary
            .copy(alpha = if (effectivePressed) 0.12f else 0.00f)
        role == ArcadeButtonRole.Destructive -> MaterialTheme.colorScheme.errorContainer
            .copy(alpha = if (effectivePressed) 0.75f else 0.55f)
        else -> Color.Transparent
    }

    val targetLabel = when {
        !enabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
        role == ArcadeButtonRole.Primary -> MaterialTheme.colorScheme.onPrimaryContainer
        role == ArcadeButtonRole.Secondary ->
            if (effectivePressed) primary
            else MaterialTheme.colorScheme.onSurfaceVariant
        role == ArcadeButtonRole.Ghost ->
            if (effectivePressed) primary
            else primary.copy(alpha = 0.80f)
        role == ArcadeButtonRole.Destructive -> MaterialTheme.colorScheme.onErrorContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    val borderStart = when {
        !enabled -> tertiary.copy(alpha = 0.15f)
        role == ArcadeButtonRole.Primary ->
            primary.copy(alpha = if (effectivePressed) 1.00f else 0.85f)
        role == ArcadeButtonRole.Secondary ->
            tertiary.copy(alpha = if (effectivePressed) 0.55f else 0.30f)
        role == ArcadeButtonRole.Ghost ->
            primary.copy(alpha = if (effectivePressed) 0.85f else 0.55f)
        role == ArcadeButtonRole.Destructive ->
            error.copy(alpha = if (effectivePressed) 1.00f else 0.80f)
        else -> tertiary.copy(alpha = 0.30f)
    }

    val borderEnd = when {
        !enabled -> Color.Transparent
        role == ArcadeButtonRole.Primary ->
            tertiary.copy(alpha = if (effectivePressed) 0.65f else 0.40f)
        role == ArcadeButtonRole.Secondary -> Color.Transparent
        role == ArcadeButtonRole.Ghost ->
            tertiary.copy(alpha = if (effectivePressed) 0.40f else 0.20f)
        role == ArcadeButtonRole.Destructive ->
            error.copy(alpha = if (effectivePressed) 0.50f else 0.25f)
        else -> Color.Transparent
    }

    val containerColor by animateColorAsState(
        targetValue = targetContainer,
        animationSpec = tween(AnimDuration),
        label = "btnContainer"
    )
    val labelColor by animateColorAsState(
        targetValue = targetLabel,
        animationSpec = tween(AnimDuration),
        label = "btnLabel"
    )
    val scale by animateFloatAsState(
        targetValue = if (effectivePressed) PressScale else 1f,
        animationSpec = tween(AnimDuration),
        label = "btnScale"
    )

    val borderBrush = Brush.linearGradient(listOf(borderStart, borderEnd))

    val height  = when (size) {
        ArcadeButtonSize.Small   -> ButtonHeightSmall
        ArcadeButtonSize.Default -> ButtonHeightDefault
        ArcadeButtonSize.Large   -> ButtonHeightLarge
    }
    val hPad: Dp = when (size) {
        ArcadeButtonSize.Small   -> ButtonHPadSmall
        ArcadeButtonSize.Default -> ButtonHPadDefault
        ArcadeButtonSize.Large   -> ButtonHPadLarge
    }
    val textStyle = when (size) {
        ArcadeButtonSize.Small   -> MaterialTheme.typography.labelMedium
        ArcadeButtonSize.Default -> MaterialTheme.typography.labelLarge
        ArcadeButtonSize.Large   -> MaterialTheme.typography.titleSmall
    }

    Box(
        modifier = modifier
            .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier)
            .scale(scale)
            .height(height)
            .clip(CircleShape)
            .background(containerColor, CircleShape)
            .border(1.dp, borderBrush, CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = hPad),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                Icon(
                    painter = leadingIcon,
                    contentDescription = null,
                    tint = labelColor,
                    modifier = Modifier.size(ButtonIconSize)
                )
                Spacer(Modifier.width(ButtonIconSpacing))
            }

            Text(
                text = label,
                style = textStyle.copy(
                    fontWeight = when (role) {
                        ArcadeButtonRole.Secondary, ArcadeButtonRole.Ghost -> FontWeight.Medium
                        else -> FontWeight.SemiBold
                    }
                ),
                color = labelColor,
                maxLines = 1
            )

            if (trailingIcon != null) {
                Spacer(Modifier.width(ButtonIconSpacing))
                Icon(
                    painter = trailingIcon,
                    contentDescription = null,
                    tint = labelColor,
                    modifier = Modifier.size(ButtonIconSize)
                )
            }
        }
    }
}

/**
 * Square icon-only variant — same token system as [ArcadeButton].
 * Size is fixed to [ButtonHeightDefault] × [ButtonHeightDefault] (48dp square).
 */
@Composable
fun ArcadeIconButton(
    onClick: () -> Unit,
    icon: Painter,
    contentDescription: String,
    modifier: Modifier = Modifier,
    role: ArcadeButtonRole = ArcadeButtonRole.Secondary,
    enabled: Boolean = true,
    buttonSize: Dp = ButtonHeightDefault
) {
    val primary  = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val error    = MaterialTheme.colorScheme.error

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val effectivePressed = isPressed && enabled

    val targetContainer = when {
        !enabled -> MaterialTheme.colorScheme.surfaceContainerLow
        role == ArcadeButtonRole.Primary ->
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = if (effectivePressed) 0.92f else 0.72f)
        role == ArcadeButtonRole.Secondary -> MaterialTheme.colorScheme.surfaceContainerLow
        role == ArcadeButtonRole.Ghost ->
            primary.copy(alpha = if (effectivePressed) 0.12f else 0.00f)
        role == ArcadeButtonRole.Destructive ->
            MaterialTheme.colorScheme.errorContainer.copy(alpha = if (effectivePressed) 0.75f else 0.55f)
        else -> Color.Transparent
    }

    val targetIcon = when {
        !enabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
        role == ArcadeButtonRole.Primary ->
            if (effectivePressed) primary else MaterialTheme.colorScheme.onPrimaryContainer
        role == ArcadeButtonRole.Secondary ->
            if (effectivePressed) primary else MaterialTheme.colorScheme.onSurfaceVariant
        role == ArcadeButtonRole.Ghost ->
            primary.copy(alpha = if (effectivePressed) 1.00f else 0.80f)
        role == ArcadeButtonRole.Destructive -> MaterialTheme.colorScheme.onErrorContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    val borderStart = when {
        !enabled -> tertiary.copy(alpha = 0.15f)
        role == ArcadeButtonRole.Primary ->
            primary.copy(alpha = if (effectivePressed) 1.00f else 0.85f)
        role == ArcadeButtonRole.Secondary ->
            tertiary.copy(alpha = if (effectivePressed) 0.55f else 0.30f)
        role == ArcadeButtonRole.Ghost ->
            primary.copy(alpha = if (effectivePressed) 0.85f else 0.55f)
        role == ArcadeButtonRole.Destructive ->
            error.copy(alpha = if (effectivePressed) 1.00f else 0.80f)
        else -> tertiary.copy(alpha = 0.30f)
    }

    val containerColor by animateColorAsState(targetContainer, tween(AnimDuration), "iconBtnContainer")
    val iconTint       by animateColorAsState(targetIcon,      tween(AnimDuration), "iconBtnTint")
    val scale          by animateFloatAsState(
        targetValue = if (effectivePressed) PressScale else 1f,
        animationSpec = tween(AnimDuration),
        label = "iconBtnScale"
    )

    Box(
        modifier = modifier
            .size(buttonSize)
            .scale(scale)
            .clip(CircleShape)
            .background(containerColor, CircleShape)
            .border(
                1.dp,
                Brush.linearGradient(listOf(borderStart, Color.Transparent)),
                CircleShape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(ButtonIconSize)
        )
    }
}

/**
 * Lays out 2–3 [ArcadeButton]s in a horizontal row with consistent spacing.
 * Typical use: dialog footers, confirmation bars.
 */
@Composable
fun ArcadeButtonRow(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier.padding(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ArcadeButtonRolesPreview() {
    OpenArcadeTheme {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ArcadeButton(
                onClick = {},
                label = "Launch Game",
                role = ArcadeButtonRole.Primary,
                leadingIcon = painterResource(R.drawable.round_sports_esports_24)
            )
            ArcadeButton(
                onClick = {},
                label = "View Details",
                role = ArcadeButtonRole.Secondary,
                leadingIcon = painterResource(R.drawable.round_explore_24)
            )
            ArcadeButton(
                onClick = {},
                label = "Add to Favorites",
                role = ArcadeButtonRole.Ghost,
                leadingIcon = painterResource(R.drawable.round_favorite_24)
            )
            ArcadeButton(
                onClick = {},
                label = "Uninstall",
                role = ArcadeButtonRole.Destructive,
                leadingIcon = painterResource(R.drawable.round_close_24)
            )
            ArcadeButton(
                onClick = {},
                label = "Disabled",
                role = ArcadeButtonRole.Primary,
                enabled = false
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ArcadeButtonSizesPreview() {
    OpenArcadeTheme {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ArcadeButton(
                onClick = {},
                label = "Small",
                size = ArcadeButtonSize.Small,
                role = ArcadeButtonRole.Primary
            )
            ArcadeButton(
                onClick = {},
                label = "Default",
                size = ArcadeButtonSize.Default,
                role = ArcadeButtonRole.Primary
            )
            ArcadeButton(
                onClick = {},
                label = "Large",
                size = ArcadeButtonSize.Large,
                role = ArcadeButtonRole.Primary,
                fullWidth = true,
                trailingIcon = painterResource(R.drawable.round_arrow_back_24)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ArcadeIconButtonPreview() {
    OpenArcadeTheme {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ArcadeIconButton(
                onClick = {},
                icon = painterResource(R.drawable.round_favorite_24),
                contentDescription = "Favorite",
                role = ArcadeButtonRole.Primary
            )
            ArcadeIconButton(
                onClick = {},
                icon = painterResource(R.drawable.round_explore_24),
                contentDescription = "Explore",
                role = ArcadeButtonRole.Secondary
            )
            ArcadeIconButton(
                onClick = {},
                icon = painterResource(R.drawable.round_bolt_24),
                contentDescription = "Boost",
                role = ArcadeButtonRole.Ghost
            )
            ArcadeIconButton(
                onClick = {},
                icon = painterResource(R.drawable.round_close_24),
                contentDescription = "Remove",
                role = ArcadeButtonRole.Destructive
            )
            ArcadeIconButton(
                onClick = {},
                icon = painterResource(R.drawable.round_check_24),
                contentDescription = "Disabled",
                role = ArcadeButtonRole.Primary,
                enabled = false
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ArcadeButtonRowPreview() {
    OpenArcadeTheme {
        ArcadeButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            ArcadeButton(
                onClick = {},
                label = "Cancel",
                role = ArcadeButtonRole.Secondary
            )
            ArcadeButton(
                onClick = {},
                label = "Remove",
                role = ArcadeButtonRole.Destructive
            )
            ArcadeButton(
                onClick = {},
                label = "Confirm",
                role = ArcadeButtonRole.Primary,
                leadingIcon = painterResource(R.drawable.round_check_24)
            )
        }
    }
}