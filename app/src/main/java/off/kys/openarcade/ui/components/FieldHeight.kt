package off.kys.openarcade.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import off.kys.openarcade.R
import off.kys.openarcade.ui.theme.OpenArcadeTheme

private val FieldHeight = 52.dp
private val FieldHeightSmall = 42.dp
private val FieldHPad = 16.dp
private val FieldHPadSmall = 12.dp
private val FieldIconSize = 18.dp
private val FieldIconSpacing = 10.dp
private val FieldCornerDefault = 16.dp
private val FieldCornerPill = 999.dp
private const val AnimDuration = 200

enum class ArcadeFieldShape {
    /** Rounded rectangle — used for multi-line or form fields. */
    Rounded,

    /** Full pill — used for search bars and inline filters. */
    Pill
}

private enum class ArcadeFieldState {
    Idle, Focused, Error, Disabled
}

/**
 * Arcade-styled single-line text field.
 *
 * Follows the gradient-border / press-feedback language of the component library:
 * · Idle     : tertiary 0.30f border, surfaceContainerLow fill
 * · Focused  : primary→tertiary gradient border, primary 0.07f wash
 * · Error    : error 0.80f border, errorContainer 0.18f wash
 * · Disabled : tertiary 0.15f border, 38% label alpha
 *
 * @param value             Current text value.
 * @param onValueChange     Called on every keystroke.
 * @param modifier          Applied to the outermost Column.
 * @param label             Floating label shown above the field when focused/filled.
 * @param placeholder       Hint text shown when the field is empty.
 * @param leadingIcon       Optional icon on the left.
 * @param trailingIcon      Optional icon on the right (e.g. clear button).
 * @param onTrailingClick   Called when the trailing icon is tapped.
 * @param isError           When true applies error styling.
 * @param errorMessage      Shown below the field when [isError] is true.
 * @param enabled           When false the field is dimmed and non-interactive.
 * @param singleLine        Constrains input to one line (default true).
 * @param fieldShape        [ArcadeFieldShape.Rounded] or [ArcadeFieldShape.Pill].
 * @param keyboardOptions   Forwarded to [BasicTextField].
 * @param keyboardActions   Forwarded to [BasicTextField].
 * @param visualTransformation Forwarded to [BasicTextField] (e.g. password masking).
 */
@Composable
fun ArcadeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    onTrailingClick: (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    isSmall: Boolean = false,
    fieldShape: ArcadeFieldShape = ArcadeFieldShape.Rounded,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val error = MaterialTheme.colorScheme.error

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val state = when {
        !enabled -> ArcadeFieldState.Disabled
        isError -> ArcadeFieldState.Error
        isFocused -> ArcadeFieldState.Focused
        else -> ArcadeFieldState.Idle
    }

    val shape = when (fieldShape) {
        ArcadeFieldShape.Rounded -> RoundedCornerShape(FieldCornerDefault)
        ArcadeFieldShape.Pill -> RoundedCornerShape(FieldCornerPill)
    }

    val fieldHeight = if (isSmall) FieldHeightSmall else FieldHeight
    val fieldHPad = if (isSmall) FieldHPadSmall else FieldHPad

    val containerColor by animateColorAsState(
        targetValue = when (state) {
            ArcadeFieldState.Focused -> primary.copy(alpha = 0.07f)
            ArcadeFieldState.Error -> error.copy(alpha = 0.08f)
            ArcadeFieldState.Disabled -> MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.55f)
            ArcadeFieldState.Idle -> MaterialTheme.colorScheme.surfaceContainerLow
        },
        animationSpec = tween(AnimDuration),
        label = "fieldContainer"
    )

    val borderStartColor by animateColorAsState(
        targetValue = when (state) {
            ArcadeFieldState.Focused -> primary.copy(alpha = 0.90f)
            ArcadeFieldState.Error -> error.copy(alpha = 0.85f)
            ArcadeFieldState.Disabled -> tertiary.copy(alpha = 0.15f)
            ArcadeFieldState.Idle -> tertiary.copy(alpha = 0.35f)
        },
        animationSpec = tween(AnimDuration),
        label = "fieldBorderStart"
    )

    val borderEndColor by animateColorAsState(
        targetValue = when (state) {
            ArcadeFieldState.Focused -> tertiary.copy(alpha = 0.45f)
            ArcadeFieldState.Error -> error.copy(alpha = 0.30f)
            ArcadeFieldState.Disabled -> Color.Transparent
            ArcadeFieldState.Idle -> Color.Transparent
        },
        animationSpec = tween(AnimDuration),
        label = "fieldBorderEnd"
    )

    val labelColor by animateColorAsState(
        targetValue = when (state) {
            ArcadeFieldState.Focused -> primary
            ArcadeFieldState.Error -> error
            ArcadeFieldState.Disabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
            ArcadeFieldState.Idle -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(AnimDuration),
        label = "fieldLabel"
    )

    val iconTint by animateColorAsState(
        targetValue = when (state) {
            ArcadeFieldState.Focused -> primary
            ArcadeFieldState.Error -> error
            ArcadeFieldState.Disabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
            ArcadeFieldState.Idle -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.70f)
        },
        animationSpec = tween(AnimDuration),
        label = "fieldIcon"
    )

    // Label floats up when focused or has content
    val labelFloated = isFocused || value.isNotEmpty()
    val labelScale by animateFloatAsState(
        targetValue = if (labelFloated) 0.78f else 1.00f,
        animationSpec = tween(AnimDuration),
        label = "labelScale"
    )
    val labelOffsetY by animateDpAsState(
        targetValue = if (labelFloated) (-8).dp else 0.dp,
        animationSpec = tween(AnimDuration),
        label = "labelOffsetY"
    )

    // 2dp bottom accent line — visible when focused, mirrors 2dp card divider
    val accentAlpha by animateFloatAsState(
        targetValue = when (state) {
            ArcadeFieldState.Focused -> 1.00f
            ArcadeFieldState.Error -> 0.80f
            else -> 0.00f
        },
        animationSpec = tween(AnimDuration),
        label = "accentAlpha"
    )

    val borderBrush = Brush.linearGradient(listOf(borderStartColor, borderEndColor))
    val cursorBrush = SolidColor(if (isError) error else primary)

    Column(modifier = modifier) {

        if (label != null) {
            Box(
                modifier = Modifier
                    .padding(start = fieldHPad, bottom = 4.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = labelColor,
                    modifier = Modifier.graphicsLayer {
                        scaleX = labelScale
                        scaleY = labelScale
                        translationY = labelOffsetY.toPx()
                        transformOrigin = TransformOrigin(0f, 0.5f)
                    }
                )
            }
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            singleLine = singleLine,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = if (enabled)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                fontWeight = FontWeight.Normal
            ),
            cursorBrush = cursorBrush,
            interactionSource = interactionSource,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            decorationBox = { innerTextField ->
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(fieldHeight)
                            .clip(shape)
                            .background(containerColor, shape)
                            .border(1.dp, borderBrush, shape)
                            .padding(horizontal = fieldHPad)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (leadingIcon != null) {
                                Icon(
                                    painter = leadingIcon,
                                    contentDescription = null,
                                    tint = iconTint,
                                    modifier = Modifier.size(FieldIconSize)
                                )
                                Spacer(Modifier.width(FieldIconSpacing))
                            }

                            Box(modifier = Modifier.weight(1f)) {
                                if (value.isEmpty() && placeholder != null) {
                                    Text(
                                        text = placeholder,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                            .copy(alpha = if (enabled) 0.50f else 0.30f),
                                        maxLines = 1
                                    )
                                }
                                innerTextField()
                            }

                            if (trailingIcon != null) {
                                Spacer(Modifier.width(FieldIconSpacing))
                                Box(
                                    modifier = Modifier
                                        .size(FieldIconSize + 8.dp)
                                        .clip(CircleShape)
                                        .then(
                                            if (onTrailingClick != null)
                                                Modifier.background(
                                                    iconTint.copy(alpha = 0.10f),
                                                    CircleShape
                                                )
                                            else Modifier
                                        )
                                        .then(
                                            if (onTrailingClick != null)
                                                Modifier.padding(4.dp)
                                            else Modifier
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = trailingIcon,
                                        contentDescription = null,
                                        tint = iconTint,
                                        modifier = Modifier
                                            .size(FieldIconSize)
                                            .then(
                                                if (onTrailingClick != null)
                                                    Modifier
                                                        .clip(CircleShape)
                                                        .alpha(if (value.isNotEmpty()) 1f else 0f)
                                                else Modifier
                                            )
                                    )
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .fillMaxWidth()
                            .height(2.dp)
                            .alpha(accentAlpha)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        (if (isError) error else primary).copy(alpha = 0.80f),
                                        tertiary.copy(alpha = 0.40f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }
            }
        )

        if (isError && errorMessage != null) {
            Spacer(Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = fieldHPad)
            ) {
                Icon(
                    painter = painterResource(R.drawable.round_close_24),
                    contentDescription = null,
                    tint = error,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.labelSmall,
                    color = error
                )
            }
        }
    }
}

/**
 * Pill-shaped search field — thin wrapper around [ArcadeTextField] with
 * search icon pre-wired and clear button shown when the query is non-empty.
 */
@Composable
fun ArcadeSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search…",
    enabled: Boolean = true,
    isSmall: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Search
    ),
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    ArcadeTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder,
        leadingIcon = painterResource(R.drawable.round_explore_24),
        trailingIcon = if (value.isNotEmpty())
            painterResource(R.drawable.round_close_24)
        else null,
        onTrailingClick = { onValueChange("") },
        enabled = enabled,
        isSmall = isSmall,
        fieldShape = ArcadeFieldShape.Pill,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions
    )
}

/**
 * Multi-line variant — same visual tokens, taller surface, no pill option.
 *
 * @param minLines  Minimum visible lines (default 3).
 * @param maxLines  Maximum lines before scrolling (default 6).
 */
@Composable
fun ArcadeTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    isSmall: Boolean = false,
    minLines: Int = 3,
    maxLines: Int = 6
) {
    val fieldHPad = if (isSmall) FieldHPadSmall else FieldHPad

    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val error = MaterialTheme.colorScheme.error

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val state = when {
        !enabled -> ArcadeFieldState.Disabled
        isError -> ArcadeFieldState.Error
        isFocused -> ArcadeFieldState.Focused
        else -> ArcadeFieldState.Idle
    }

    val containerColor by animateColorAsState(
        targetValue = when (state) {
            ArcadeFieldState.Focused -> primary.copy(alpha = 0.07f)
            ArcadeFieldState.Error -> error.copy(alpha = 0.08f)
            ArcadeFieldState.Disabled -> MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.55f)
            ArcadeFieldState.Idle -> MaterialTheme.colorScheme.surfaceContainerLow
        },
        animationSpec = tween(AnimDuration),
        label = "areaContainer"
    )
    val borderStartColor by animateColorAsState(
        targetValue = when (state) {
            ArcadeFieldState.Focused -> primary.copy(alpha = 0.90f)
            ArcadeFieldState.Error -> error.copy(alpha = 0.85f)
            ArcadeFieldState.Disabled -> tertiary.copy(alpha = 0.15f)
            ArcadeFieldState.Idle -> tertiary.copy(alpha = 0.35f)
        },
        animationSpec = tween(AnimDuration),
        label = "areaBorderStart"
    )
    val borderEndColor by animateColorAsState(
        targetValue = when (state) {
            ArcadeFieldState.Focused -> tertiary.copy(alpha = 0.45f)
            ArcadeFieldState.Error -> error.copy(alpha = 0.30f)
            else -> Color.Transparent
        },
        animationSpec = tween(AnimDuration),
        label = "areaBorderEnd"
    )
    val labelColor by animateColorAsState(
        targetValue = when (state) {
            ArcadeFieldState.Focused -> primary
            ArcadeFieldState.Error -> error
            ArcadeFieldState.Disabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
            ArcadeFieldState.Idle -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(AnimDuration),
        label = "areaLabel"
    )
    val accentAlpha by animateFloatAsState(
        targetValue = if (state == ArcadeFieldState.Focused || state == ArcadeFieldState.Error)
            1f else 0f,
        animationSpec = tween(AnimDuration),
        label = "areaAccent"
    )

    val shape = RoundedCornerShape(FieldCornerDefault)

    Column(modifier = modifier) {
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = labelColor,
                modifier = Modifier.padding(start = fieldHPad, bottom = 4.dp)
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            singleLine = false,
            minLines = minLines,
            maxLines = maxLines,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = if (enabled)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            ),
            cursorBrush = SolidColor(if (isError) error else primary),
            interactionSource = interactionSource,
            decorationBox = { innerTextField ->
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape)
                            .background(containerColor, shape)
                            .border(
                                1.dp,
                                Brush.linearGradient(listOf(borderStartColor, borderEndColor)),
                                shape
                            )
                            .padding(fieldHPad)
                    ) {
                        if (value.isEmpty() && placeholder != null) {
                            Text(
                                text = placeholder,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                    .copy(alpha = if (enabled) 0.50f else 0.30f)
                            )
                        }
                        innerTextField()
                    }

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .fillMaxWidth()
                            .height(2.dp)
                            .alpha(accentAlpha)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        (if (isError) error else primary).copy(alpha = 0.80f),
                                        tertiary.copy(alpha = 0.40f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }
            }
        )

        if (isError && errorMessage != null) {
            Spacer(Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = fieldHPad)
            ) {
                Icon(
                    painter = painterResource(R.drawable.round_close_24),
                    contentDescription = null,
                    tint = error,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.labelSmall,
                    color = error
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ArcadeTextFieldStatesPreview() {
    OpenArcadeTheme {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Idle
            var idle by remember { mutableStateOf("") }
            ArcadeTextField(
                value = idle,
                onValueChange = { idle = it },
                label = "Username",
                placeholder = "Enter your username",
                leadingIcon = painterResource(R.drawable.round_explore_24),
                modifier = Modifier.fillMaxWidth()
            )

            // Filled
            ArcadeTextField(
                value = "ArcadePlayer1",
                onValueChange = {},
                label = "Display name",
                leadingIcon = painterResource(R.drawable.round_sports_esports_24),
                trailingIcon = painterResource(R.drawable.round_close_24),
                onTrailingClick = {},
                modifier = Modifier.fillMaxWidth()
            )

            // Error
            ArcadeTextField(
                value = "bad@",
                onValueChange = {},
                label = "Email",
                leadingIcon = painterResource(R.drawable.round_explore_24),
                isError = true,
                errorMessage = "Enter a valid email address",
                modifier = Modifier.fillMaxWidth()
            )

            // Disabled
            ArcadeTextField(
                value = "locked.user",
                onValueChange = {},
                label = "Account",
                leadingIcon = painterResource(R.drawable.round_bolt_24),
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )

            // Password
            ArcadeTextField(
                value = "secret123",
                onValueChange = {},
                label = "Password",
                leadingIcon = painterResource(R.drawable.round_favorite_24),
                trailingIcon = painterResource(R.drawable.round_check_24),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ArcadeSearchFieldPreview() {
    OpenArcadeTheme {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Empty
            ArcadeSearchField(
                value = "",
                onValueChange = {},
                placeholder = "Search games…",
                modifier = Modifier.fillMaxWidth()
            )
            // Filled
            ArcadeSearchField(
                value = "Arcade Racer",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ArcadeTextAreaPreview() {
    OpenArcadeTheme {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ArcadeTextArea(
                value = "",
                onValueChange = {},
                label = "Notes",
                placeholder = "Add a note about this game…",
                modifier = Modifier.fillMaxWidth()
            )
            ArcadeTextArea(
                value = "Great platformer with tight controls and excellent level design.",
                onValueChange = {},
                label = "Review",
                modifier = Modifier.fillMaxWidth()
            )
            ArcadeTextArea(
                value = "Too short",
                onValueChange = {},
                label = "Review",
                isError = true,
                errorMessage = "Review must be at least 20 characters",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}