package off.kys.openarcade.ui.launcher.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import off.kys.openarcade.R
import off.kys.openarcade.domain.model.GameEntry
import off.kys.openarcade.ui.components.ArcadeCard
import off.kys.openarcade.ui.components.ArcadeGameIcon
import off.kys.openarcade.ui.components.SectionHeader
import off.kys.openarcade.util.ColorExtractor

@Composable
fun FavoritesSection(
    games: List<GameEntry>,
    onGameClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    hapticFeedbackEnabled: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth()) {

        SectionHeader(
            title = stringResource(R.string.favorites),
            icon = painterResource(R.drawable.round_favorite_24),
            pulseIcon = true,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 4.dp, end = 4.dp)
        ) {
            items(games, key = { "fav_${it.packageName}" }) { game ->
                FavoriteGameCard(
                    game = game,
                    onClick = { onGameClick(game.packageName) },
                    hapticFeedbackEnabled = hapticFeedbackEnabled
                )
            }
        }
    }
}

@Composable
private fun FavoriteGameCard(
    game: GameEntry,
    onClick: () -> Unit,
    hapticFeedbackEnabled: Boolean = true
) {
    val haptic = LocalHapticFeedback.current
    val isDark = isSystemInDarkTheme()
    val adaptivePrimary = ColorExtractor.getAdaptiveColor(game.getPrimaryColor(), isDark)
    val adaptiveTertiary = ColorExtractor.getAdaptiveColor(game.getTertiaryColor(), isDark)

    ArcadeCard(
        onClick = {
            if (hapticFeedbackEnabled) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
            onClick()
        },
        modifier = Modifier.size(72.dp),
        accentColor = adaptiveTertiary,
    ) {
        ArcadeGameIcon(
            icon = game.customIconPath ?: game.cachedIconPath ?: game.packageName,
            contentDescription = game.displayName,
            modifier = Modifier.fillMaxSize(),
            primaryColor = adaptivePrimary,
            tertiaryColor = adaptiveTertiary,
            iconSize = 44.dp
        )
    }
}