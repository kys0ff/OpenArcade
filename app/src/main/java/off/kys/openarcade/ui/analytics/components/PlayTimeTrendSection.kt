package off.kys.openarcade.ui.analytics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import off.kys.openarcade.R
import off.kys.openarcade.domain.model.PlayTimePoint
import off.kys.openarcade.ui.analytics.AnalyticsInterval
import off.kys.openarcade.ui.components.ArcadeCard
import off.kys.openarcade.ui.components.BarChart
import off.kys.openarcade.ui.components.SectionHeader
import off.kys.openarcade.ui.launcher.components.ArcadeFilterChip

@Composable
fun PlayTimeTrendSection(
    selectedInterval: AnalyticsInterval,
    chartData: List<PlayTimePoint>,
    barColor: Color,
    onIntervalSelected: (AnalyticsInterval) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionHeader(
                title = stringResource(R.string.play_time_trend),
                modifier = Modifier.weight(1f)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                ArcadeFilterChip(
                    label = stringResource(R.string.daily),
                    selected = selectedInterval == AnalyticsInterval.DAILY,
                    onClick = { onIntervalSelected(AnalyticsInterval.DAILY) }
                )
                ArcadeFilterChip(
                    label = stringResource(R.string.weekly),
                    selected = selectedInterval == AnalyticsInterval.WEEKLY,
                    onClick = { onIntervalSelected(AnalyticsInterval.WEEKLY) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        ArcadeCard(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.padding(16.dp)) {
                BarChart(
                    data = chartData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .padding(bottom = 8.dp),
                    barColor = barColor
                )
            }
        }
    }
}
