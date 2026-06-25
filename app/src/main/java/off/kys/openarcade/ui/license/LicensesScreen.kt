package off.kys.openarcade.ui.license

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import off.kys.openarcade.R
import off.kys.openarcade.ui.components.ArcadeLicenseScrollbar
import off.kys.openarcade.ui.components.ArcadeSearchField
import off.kys.openarcade.ui.components.CategoryAccordion
import off.kys.openarcade.ui.components.LicenseCategory
import off.kys.openarcade.ui.components.LicenseCountBanner
import off.kys.openarcade.ui.components.ScrollbarPad
import off.kys.openarcade.ui.components.ScrollbarWidth
import off.kys.openarcade.ui.components.SectionHeader
import off.kys.openarcade.ui.components.libraries
import off.kys.openarcade.ui.theme.OpenArcadeTheme

class LicensesScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val primary = MaterialTheme.colorScheme.primary
        val tertiary = MaterialTheme.colorScheme.tertiary
        val listState = rememberLazyListState()

        var searchQuery by remember { mutableStateOf("") }
        var expandedCategory by remember { mutableStateOf<LicenseCategory?>(null) }

        val filtered = remember(searchQuery) {
            if (searchQuery.isBlank()) libraries
            else libraries.mapValues { (_, libs) ->
                libs.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                            it.author.contains(searchQuery, ignoreCase = true) ||
                            it.licenseType.label.contains(searchQuery, ignoreCase = true)
                }
            }.filter { it.value.isNotEmpty() }
        }

        val totalCount = libraries.values.sumOf { it.size }

        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Licenses",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black
                                )
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { navigator.pop() }) {
                                Icon(
                                    painter = painterResource(R.drawable.round_arrow_back_24),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        primary.copy(alpha = 0.65f),
                                        tertiary.copy(alpha = 0.30f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp + ScrollbarWidth + ScrollbarPad * 2,
                        top = 16.dp,
                        bottom = 32.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {

                    item {
                        ArcadeSearchField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = "Search libraries…",
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(12.dp))
                        LicenseCountBanner(totalCount = totalCount, filtered = filtered)
                        Spacer(Modifier.height(16.dp))
                    }

                    filtered.entries.forEach { (category, libs) ->
                        item(key = category.name) {
                            CategoryAccordion(
                                category = category,
                                libraries = libs,
                                isExpanded = expandedCategory == category,
                                onToggle = {
                                    expandedCategory =
                                        if (expandedCategory == category) null else category
                                }
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }

                ArcadeLicenseScrollbar(
                    listState = listState,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxSize()
                        .padding(end = ScrollbarPad)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun LicensesScreenPreview() {
    OpenArcadeTheme {
        var expandedCategory by remember { mutableStateOf<LicenseCategory?>(LicenseCategory.Ui) }
        val listState = rememberLazyListState()

        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    SectionHeader(title = "Open Source Licenses")
                    Spacer(Modifier.height(12.dp))
                    LicenseCountBanner(
                        totalCount = libraries.values.sumOf { it.size },
                        filtered = libraries
                    )
                    Spacer(Modifier.height(8.dp))
                }
                items(libraries.entries.toList(), key = { it.key.name }) { (cat, libs) ->
                    CategoryAccordion(
                        category = cat,
                        libraries = libs,
                        isExpanded = expandedCategory == cat,
                        onToggle = {
                            expandedCategory = if (expandedCategory == cat) null else cat
                        }
                    )
                }
            }
            ArcadeLicenseScrollbar(
                listState = listState,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxSize()
                    .padding(end = ScrollbarPad)
            )
        }
    }
}
