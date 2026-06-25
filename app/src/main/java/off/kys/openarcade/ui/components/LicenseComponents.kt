package off.kys.openarcade.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import off.kys.openarcade.R

val CardHPad = 16.dp
val CardVPad = 14.dp
val BadgeSize = 36.dp
val BadgeIconSize = 18.dp
val ChevronSize = 14.dp
const val AnimDur = 200

enum class LicenseType(val label: String) {
    Apache2("Apache 2.0"),
    Mit("MIT"),
    Bsd2("BSD-2"),
    Bsd3("BSD-3"),
    Isc("ISC"),
    Lgpl("LGPL-2.1"),
    Mpl2("MPL 2.0")
}

data class LibraryLicense(
    val name: String,
    val author: String,
    val version: String,
    val licenseType: LicenseType,
    val description: String,
    val url: String
)

enum class LicenseCategory(val label: String, val iconRes: Int) {
    Ui("UI & Compose", R.drawable.round_category_24),
    Navigation("Navigation", R.drawable.round_explore_24),
    Di("Dependency Injection", R.drawable.round_bolt_24),
    Networking("Networking & Images", R.drawable.round_sports_esports_24),
    Database("Database", R.drawable.round_history_24),
    Analytics("Analytics", R.drawable.round_bar_chart_24),
    Utility("Utilities", R.drawable.round_check_24)
}

val libraries: Map<LicenseCategory, List<LibraryLicense>> = mapOf(
    LicenseCategory.Ui to listOf(
        LibraryLicense(
            "Jetpack Compose", "Google", "1.7.0", LicenseType.Apache2,
            "Modern UI toolkit for building native Android interfaces declaratively.",
            "https://developer.android.com/jetpack/compose"
        ),
        LibraryLicense(
            "Material3", "Google", "1.3.0", LicenseType.Apache2,
            "Material Design 3 components for Compose.",
            "https://m3.material.io"
        ),
        LibraryLicense(
            "Cloudy", "skydoves", "0.5.0", LicenseType.Apache2,
            "Jetpack Compose blur effect library.",
            "https://github.com/skydoves/cloudy"
        ),
    ),
    LicenseCategory.Navigation to listOf(
        LibraryLicense(
            "Voyager", "Adriel Café", "1.1.0", LicenseType.Mit,
            "A pragmatic navigation library for Jetpack Compose.",
            "https://voyager.adriel.cafe"
        ),
    ),
    LicenseCategory.Di to listOf(
        LibraryLicense(
            "Koin", "InsertKoin", "4.0.0", LicenseType.Apache2,
            "Lightweight dependency injection framework for Kotlin.",
            "https://insert-koin.io"
        ),
    ),
    LicenseCategory.Networking to listOf(
        LibraryLicense(
            "Coil", "Coil Contributors", "2.7.0", LicenseType.Apache2,
            "Image loading library for Android backed by Kotlin coroutines.",
            "https://coil-kt.github.io/coil"
        ),
    ),
    LicenseCategory.Database to listOf(
        LibraryLicense(
            "Room", "Google", "2.6.1", LicenseType.Apache2,
            "Persistence library providing an abstraction layer over SQLite.",
            "https://developer.android.com/training/data-storage/room"
        ),
    ),
    LicenseCategory.Analytics to listOf(
        LibraryLicense(
            "Kotlin Coroutines", "JetBrains", "1.9.0", LicenseType.Apache2,
            "Asynchronous programming with coroutines for Kotlin.",
            "https://kotlinlang.org/docs/coroutines-overview.html"
        ),
        LibraryLicense(
            "KotlinX Serialization", "JetBrains", "1.7.0", LicenseType.Apache2,
            "Kotlin multiplatform / multi-format serialization.",
            "https://github.com/Kotlin/kotlinx.serialization"
        ),
    ),
    LicenseCategory.Utility to listOf(
        LibraryLicense(
            "Palette", "Google", "1.0.0", LicenseType.Apache2,
            "Extract prominent colors from images.",
            "https://developer.android.com/training/material/palette-colors"
        ),
        LibraryLicense(
            "Core KTX", "Google", "1.13.0", LicenseType.Apache2,
            "Kotlin extensions for Android Core.",
            "https://developer.android.com/kotlin/ktx"
        ),
        LibraryLicense(
            "Splashscreen", "Google", "1.0.1", LicenseType.Apache2,
            "Android Splash Screen library.",
            "https://developer.android.com/guide/topics/ui/splash-screen"
        )
    )
)

@Composable
fun LicenseCountBanner(
    totalCount: Int,
    filtered: Map<LicenseCategory, List<LibraryLicense>>
) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val filteredCount = filtered.values.sumOf { it.size }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .border(
                1.dp,
                Brush.linearGradient(
                    listOf(tertiary.copy(alpha = 0.40f), Color.Transparent)
                ),
                MaterialTheme.shapes.medium
            )
            .padding(horizontal = CardHPad, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(BadgeSize)
                .clip(MaterialTheme.shapes.small)
                .background(
                    Brush.radialGradient(
                        listOf(primary.copy(alpha = 0.18f), primary.copy(alpha = 0.04f))
                    )
                )
                .border(
                    1.dp,
                    Brush.linearGradient(listOf(primary.copy(alpha = 0.40f), Color.Transparent)),
                    MaterialTheme.shapes.small
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.round_category_24),
                contentDescription = null,
                tint = primary,
                modifier = Modifier.size(BadgeIconSize)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Open Source Libraries",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (filteredCount == totalCount)
                    "$totalCount libraries across ${libraries.size} categories"
                else
                    "$filteredCount of $totalCount libraries",
                style = MaterialTheme.typography.bodySmall,
                color = primary.copy(alpha = 0.75f)
            )
        }

        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(
                    Brush.horizontalGradient(
                        listOf(primary.copy(alpha = 0.72f), tertiary.copy(alpha = 0.45f))
                    )
                )
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = "$totalCount",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.surface
            )
        }
    }
}

@Composable
fun CategoryAccordion(
    category: LicenseCategory,
    libraries: List<LibraryLicense>,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary

    val headerBorderStart by animateColorAsState(
        targetValue = if (isExpanded) primary.copy(alpha = 0.75f)
        else tertiary.copy(alpha = 0.35f),
        animationSpec = tween(AnimDur),
        label = "catBorder_${category.name}"
    )
    val headerBg by animateColorAsState(
        targetValue = if (isExpanded) primary.copy(alpha = 0.07f)
        else MaterialTheme.colorScheme.surfaceContainerLow,
        animationSpec = tween(AnimDur),
        label = "catBg_${category.name}"
    )
    val badgeBg by animateColorAsState(
        targetValue = if (isExpanded) primary.copy(alpha = 0.20f)
        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
        animationSpec = tween(AnimDur),
        label = "catBadgeBg_${category.name}"
    )
    val iconTint by animateColorAsState(
        targetValue = if (isExpanded) primary
        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
        animationSpec = tween(AnimDur),
        label = "catIcon_${category.name}"
    )
    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(AnimDur),
        label = "catChevron_${category.name}"
    )
    val labelColor by animateColorAsState(
        targetValue = if (isExpanded) MaterialTheme.colorScheme.onSurface
        else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(AnimDur),
        label = "catLabel_${category.name}"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    if (isExpanded) RoundedCornerShape(
                        topStart = 16.dp, topEnd = 16.dp,
                        bottomStart = 0.dp, bottomEnd = 0.dp
                    ) else MaterialTheme.shapes.large
                )
                .background(headerBg)
                .border(
                    1.dp,
                    Brush.linearGradient(
                        listOf(headerBorderStart, Color.Transparent)
                    ),
                    if (isExpanded) RoundedCornerShape(
                        topStart = 16.dp, topEnd = 16.dp
                    ) else MaterialTheme.shapes.large
                )
        ) {
            Column {
                AnimatedVisibility(visible = isExpanded) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        primary.copy(alpha = 0.70f),
                                        tertiary.copy(alpha = 0.35f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onToggle
                        )
                        .padding(horizontal = CardHPad, vertical = CardVPad),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(BadgeSize)
                            .clip(MaterialTheme.shapes.small)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        badgeBg,
                                        badgeBg.copy(alpha = badgeBg.alpha * 0.3f)
                                    )
                                )
                            )
                            .border(
                                1.dp,
                                Brush.linearGradient(
                                    listOf(iconTint.copy(alpha = 0.40f), Color.Transparent)
                                ),
                                MaterialTheme.shapes.small
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(category.iconRes),
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(BadgeIconSize)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = category.label,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = if (isExpanded) FontWeight.SemiBold
                                else FontWeight.Medium
                            ),
                            color = labelColor
                        )
                        Text(
                            text = "${libraries.size} ${if (libraries.size == 1) "library" else "libraries"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = primary.copy(alpha = if (isExpanded) 0.80f else 0.50f)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                if (isExpanded) Brush.horizontalGradient(
                                    listOf(
                                        primary.copy(alpha = 0.85f),
                                        tertiary.copy(alpha = 0.50f)
                                    )
                                ) else Brush.linearGradient(
                                    listOf(
                                        tertiary.copy(alpha = 0.25f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .border(
                                1.dp,
                                Brush.linearGradient(
                                    listOf(headerBorderStart, Color.Transparent)
                                ),
                                CircleShape
                            )
                            .padding(horizontal = 10.dp, vertical = 3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${libraries.size}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (isExpanded) MaterialTheme.colorScheme.surface
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        painter = painterResource(R.drawable.round_arrow_back_24),
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier
                            .size(ChevronSize)
                            .rotate(chevronRotation - 90f)
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(tween(AnimDur)) + expandVertically(tween(AnimDur)),
            exit = fadeOut(tween(AnimDur)) + shrinkVertically(tween(AnimDur))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            bottomStart = 16.dp, bottomEnd = 16.dp
                        )
                    )
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .border(
                        1.dp,
                        Brush.linearGradient(
                            listOf(primary.copy(alpha = 0.20f), Color.Transparent)
                        ),
                        RoundedCornerShape(
                            bottomStart = 16.dp, bottomEnd = 16.dp
                        )
                    )
            ) {
                libraries.forEachIndexed { index, library ->
                    LibraryItem(library = library)

                    if (index < libraries.lastIndex) {
                        Box(
                            modifier = Modifier
                                .padding(
                                    start = CardHPad + BadgeSize + 12.dp,
                                    end = CardHPad
                                )
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            primary.copy(alpha = 0.12f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color.Transparent,
                                    tertiary.copy(alpha = 0.30f),
                                    primary.copy(alpha = 0.55f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
        }
    }
}

@Composable
fun LibraryItem(library: LibraryLicense) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val uriHandler = LocalUriHandler.current

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val rowBg by animateColorAsState(
        targetValue = if (isPressed) primary.copy(alpha = 0.08f) else Color.Transparent,
        animationSpec = tween(AnimDur),
        label = "libRowBg_${library.name}"
    )

    val licenseColor = when (library.licenseType) {
        LicenseType.Apache2 -> primary
        LicenseType.Mit -> MaterialTheme.colorScheme.tertiary
        LicenseType.Bsd2,
        LicenseType.Bsd3 -> MaterialTheme.colorScheme.secondary

        LicenseType.Lgpl -> MaterialTheme.colorScheme.error.copy(alpha = 0.80f)
        LicenseType.Mpl2 -> MaterialTheme.colorScheme.error.copy(alpha = 0.65f)
        LicenseType.Isc -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.80f)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBg)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { uriHandler.openUri(library.url) }
            )
            .padding(horizontal = CardHPad, vertical = 12.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(BadgeSize)
                .clip(MaterialTheme.shapes.small)
                .background(
                    Brush.radialGradient(
                        listOf(
                            licenseColor.copy(alpha = 0.20f),
                            licenseColor.copy(alpha = 0.05f)
                        )
                    )
                )
                .border(
                    1.dp,
                    Brush.linearGradient(
                        listOf(licenseColor.copy(alpha = 0.45f), Color.Transparent)
                    ),
                    MaterialTheme.shapes.small
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = library.name.first().uppercase(),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = licenseColor
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = library.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            tertiary.copy(alpha = 0.12f),
                            CircleShape
                        )
                        .border(
                            1.dp,
                            Brush.linearGradient(
                                listOf(tertiary.copy(alpha = 0.30f), Color.Transparent)
                            ),
                            CircleShape
                        )
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "v${library.version}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(2.dp))

            Text(
                text = library.author,
                style = MaterialTheme.typography.bodySmall,
                color = primary.copy(alpha = 0.75f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = library.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.70f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(licenseColor.copy(alpha = 0.12f), CircleShape)
                    .border(
                        1.dp,
                        Brush.linearGradient(
                            listOf(licenseColor.copy(alpha = 0.45f), Color.Transparent)
                        ),
                        CircleShape
                    )
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = library.licenseType.label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = licenseColor
                )
            }
        }

        Icon(
            painter = painterResource(R.drawable.round_explore_24),
            contentDescription = "Open",
            tint = primary.copy(alpha = if (isPressed) 0.80f else 0.30f),
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
fun ArcadeLicenseScrollbar(
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary

    val isScrolling = listState.isScrollInProgress
    val thumbAlpha by animateFloatAsState(
        targetValue = if (isScrolling) 0.85f else 0.25f,
        animationSpec = tween(if (isScrolling) 100 else 700),
        label = "licScrollbarAlpha"
    )

    Box(
        modifier = modifier.drawWithContent {
            drawContent()
            val total = listState.layoutInfo.totalItemsCount
            if (total == 0) return@drawWithContent
            val visible = listState.layoutInfo.visibleItemsInfo
            val first = visible.firstOrNull() ?: return@drawWithContent
            val thumbH = (size.height * visible.size.toFloat() / total)
                .coerceAtLeast(40.dp.toPx())
            val frac = first.index.toFloat() / (total - visible.size).coerceAtLeast(1)
            val thumbY = (frac * (size.height - thumbH)).coerceIn(0f, size.height - thumbH)

            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(
                        primary.copy(alpha = thumbAlpha),
                        tertiary.copy(alpha = thumbAlpha * 0.55f)
                    )
                ),
                topLeft = Offset(size.width - ScrollbarWidth.toPx(), thumbY),
                size = Size(ScrollbarWidth.toPx(), thumbH),
                cornerRadius = CornerRadius(ScrollbarWidth.toPx() / 2)
            )
        }
    )
}
