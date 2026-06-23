package off.kys.openarcade.ui.launcher.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import off.kys.openarcade.R
import off.kys.openarcade.domain.model.GameEntry
import off.kys.openarcade.ui.analytics.AnalyticsScreen
import off.kys.openarcade.ui.components.ArcadeCard
import off.kys.openarcade.ui.components.SectionHeader

@Composable
fun AnalyticsSection(games: List<GameEntry>, modifier: Modifier = Modifier) {
    val navigator = LocalNavigator.currentOrThrow
    
    val (totalPlayTimeHours, totalPlayTimeMinutes) = remember(games) {
        val totalPlayTimeMs = games.sumOf { it.totalPlayTime }
        val hours = totalPlayTimeMs / (1000 * 60 * 60)
        val minutes = (totalPlayTimeMs / (1000 * 60)) % 60
        hours to minutes
    }

    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(
            title = stringResource(R.string.analytics),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ArcadeCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = { navigator.push(AnalyticsScreen()) },
            accentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.66f)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.round_bar_chart_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(
                            text = stringResource(R.string.total_play_time),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = stringResource(R.string.items_in_filter, games.size),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (totalPlayTimeHours > 0) {
                            stringResource(R.string.play_time_hours_minutes, totalPlayTimeHours, totalPlayTimeMinutes)
                        } else {
                            stringResource(R.string.play_time_minutes, totalPlayTimeMinutes)
                        },
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.played),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
