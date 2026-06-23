package off.kys.openarcade.ui.analytics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import off.kys.openarcade.R
import off.kys.openarcade.domain.model.CategoryDistribution
import off.kys.openarcade.ui.components.ArcadeCard
import off.kys.openarcade.ui.components.DonutChart
import off.kys.openarcade.ui.components.SectionHeader

@Composable
fun CategoryDistributionSection(
    data: List<CategoryDistribution>,
    donutColors: List<Color>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SectionHeader(title = stringResource(R.string.category_distribution))
        Spacer(Modifier.height(16.dp))
        ArcadeCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DonutChart(
                    data = data,
                    modifier = Modifier.size(140.dp),
                    colors = donutColors
                )
                Spacer(Modifier.width(24.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    data.take(4).forEachIndexed { index, dist ->
                        CategoryLegendItem(
                            categoryName = stringResource(dist.category.displayNameRes),
                            percentage = (dist.percentage * 100).toInt(),
                            color = donutColors[index % donutColors.size]
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryLegendItem(
    categoryName: String,
    percentage: Int,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(8.dp),
            shape = CircleShape,
            color = color
        ) {}
        Spacer(Modifier.width(8.dp))
        Text(
            text = categoryName,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "$percentage%",
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}
