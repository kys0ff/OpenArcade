package off.kys.openarcade.ui.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import off.kys.openarcade.R
import off.kys.openarcade.domain.model.GameEntry
import off.kys.openarcade.ui.components.ArcadeButton
import off.kys.openarcade.ui.components.ArcadeButtonRole
import off.kys.openarcade.ui.components.ArcadeButtonSize

@Composable
fun GameDetailActions(
    currentGame: GameEntry,
    onLaunchClick: () -> Unit,
    onTagsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ArcadeButton(
            onClick = onLaunchClick,
            label = if (currentGame.isInstalled) stringResource(R.string.game_detail_play) 
                    else stringResource(R.string.game_detail_search_play_store),
            role = ArcadeButtonRole.Primary,
            size = ArcadeButtonSize.Large,
            enabled = true,
            fullWidth = true,
            leadingIcon = painterResource(if (currentGame.isInstalled) R.drawable.round_play_arrow_24 else R.drawable.round_explore_24)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ArcadeButton(
                onClick = onTagsClick,
                label = stringResource(R.string.game_detail_tags_button),
                role = ArcadeButtonRole.Secondary,
                size = ArcadeButtonSize.Default,
                modifier = Modifier.weight(1f),
                leadingIcon = painterResource(R.drawable.round_add_24)
            )
        }
    }
}