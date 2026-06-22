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
import androidx.compose.ui.unit.dp
import off.kys.openarcade.R
import off.kys.openarcade.domain.model.GameEntry
import off.kys.openarcade.ui.components.ArcadeCard
import off.kys.openarcade.ui.components.ArcadeGameIcon
import off.kys.openarcade.ui.components.SectionHeader
import off.kys.openarcade.util.ColorExtractor

@Composable
fun RecentActivitySection(
    games: List<GameEntry>,
    onGameClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(
            title = stringResource(R.string.recent_activity),
            icon = painterResource(R.drawable.round_history_24),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 4.dp)
        ) {
            items(games, key = { "recent_${it.packageName}" }) { game ->
                RecentGameCard(game) {
                    onGameClick(game.packageName)
                }
            }
        }
    }
}

@Composable
private fun RecentGameCard(
    game: GameEntry,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val adaptivePrimary = ColorExtractor.getAdaptiveColor(game.getPrimaryColor(), isDark)
    val adaptiveTertiary = ColorExtractor.getAdaptiveColor(game.getTertiaryColor(), isDark)

    ArcadeCard(
        onClick = onClick,
        modifier = Modifier.size(72.dp),
        accentColor = adaptiveTertiary,
    ) {
        ArcadeGameIcon(
            icon = game.customIconPath ?: game.icon,
            contentDescription = game.displayName,
            modifier = Modifier.fillMaxSize(),
            primaryColor = adaptivePrimary,
            tertiaryColor = adaptiveTertiary,
            iconSize = 44.dp
        )
    }
}