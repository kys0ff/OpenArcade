package off.kys.openarcade.ui.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import off.kys.openarcade.R
import off.kys.openarcade.domain.model.GameEntry
import off.kys.openarcade.ui.components.SectionHeader
import off.kys.openarcade.util.TimeUtils

@Composable
fun GameDetailInfoSection(
    currentGame: GameEntry,
    secondaryColor: Color,
    tertiaryColor: Color,
    onCategoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(modifier = modifier) {
        SectionHeader(
            title = stringResource(R.string.game_detail_section_details),
            accentColor = secondaryColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )

        Spacer(Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            val systemCategory = if (currentGame.category.displayNameRes != 0) {
                listOf(stringResource(currentGame.category.displayNameRes))
            } else emptyList()

            CategoryDetailRow(
                label = stringResource(R.string.game_detail_label_categories),
                categories = remember(currentGame.customCategories, currentGame.category) {
                    systemCategory + currentGame.customCategories
                },
                accentColor = tertiaryColor,
                isFirst = true,
                isLast = false,
                onClick = onCategoryClick
            )

            DetailRow(
                label = stringResource(R.string.game_detail_label_status),
                value = if (currentGame.isInstalled) {
                    stringResource(R.string.category_installed)
                } else {
                    stringResource(R.string.category_uninstalled)
                },
                valueColor = if (currentGame.isInstalled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                },
                isFirst = false,
                isLast = false,
                onClick = null
            )

            DetailRow(
                label = stringResource(R.string.game_detail_label_last_played),
                value = remember(currentGame.lastPlayed) {
                    TimeUtils.formatLastPlayed(context, currentGame.lastPlayed)
                },
                valueColor = MaterialTheme.colorScheme.onSurface,
                isFirst = false,
                isLast = false,
                onClick = null
            )

            DetailRow(
                label = stringResource(R.string.game_detail_label_play_time),
                value = remember(currentGame.totalPlayTime) {
                    TimeUtils.formatTotalPlayTime(context, currentGame.totalPlayTime)
                },
                valueColor = MaterialTheme.colorScheme.onSurface,
                isFirst = false,
                isLast = false,
                onClick = null
            )

            DetailRow(
                label = stringResource(R.string.game_detail_label_package),
                value = currentGame.packageName,
                valueColor = MaterialTheme.colorScheme.onSurface,
                isFirst = false,
                isLast = true,
                onClick = null
            )
        }
    }
}