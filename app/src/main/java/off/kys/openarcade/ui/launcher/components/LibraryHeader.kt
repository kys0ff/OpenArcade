package off.kys.openarcade.ui.launcher.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import off.kys.openarcade.R
import off.kys.openarcade.domain.model.GameSortOption
import off.kys.openarcade.ui.components.ArcadeDropdownMenu
import off.kys.openarcade.ui.components.ArcadeDropdownMenuItem

@Composable
fun LibraryHeader(
    gameCount: Int,
    selectedSort: GameSortOption,
    onSortSelected: (GameSortOption) -> Unit,
    onAddGamesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var sortMenuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.your_library),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.width(4.dp))
        Text("•")
        Spacer(Modifier.width(4.dp))
        Text(
            text = stringResource(R.string.games_count, gameCount),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.weight(1f))

        IconButton(
            onClick = onAddGamesClick,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.round_add_24),
                contentDescription = stringResource(R.string.add_games_desc),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(Modifier.width(8.dp))

        Box {
            IconButton(
                onClick = { sortMenuExpanded = true },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.round_sort_24),
                    contentDescription = stringResource(R.string.sort_button_desc),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            ArcadeDropdownMenu(
                expanded = sortMenuExpanded,
                onDismissRequest = { sortMenuExpanded = false }
            ) {
                ArcadeDropdownMenuItem(
                    text = stringResource(R.string.sort_title_asc),
                    selected = selectedSort == GameSortOption.TITLE_ASC,
                    onClick = {
                        onSortSelected(GameSortOption.TITLE_ASC)
                        sortMenuExpanded = false
                    }
                )
                ArcadeDropdownMenuItem(
                    text = stringResource(R.string.sort_title_desc),
                    selected = selectedSort == GameSortOption.TITLE_DESC,
                    onClick = {
                        onSortSelected(GameSortOption.TITLE_DESC)
                        sortMenuExpanded = false
                    }
                )
                ArcadeDropdownMenuItem(
                    text = stringResource(R.string.sort_last_played),
                    selected = selectedSort == GameSortOption.LAST_PLAYED,
                    onClick = {
                        onSortSelected(GameSortOption.LAST_PLAYED)
                        sortMenuExpanded = false
                    }
                )
                ArcadeDropdownMenuItem(
                    text = stringResource(R.string.sort_play_time),
                    selected = selectedSort == GameSortOption.TOTAL_PLAY_TIME,
                    onClick = {
                        onSortSelected(GameSortOption.TOTAL_PLAY_TIME)
                        sortMenuExpanded = false
                    }
                )
            }
        }
    }
}