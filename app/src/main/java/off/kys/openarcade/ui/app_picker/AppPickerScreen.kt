package off.kys.openarcade.ui.app_picker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import off.kys.openarcade.ui.app_picker.components.AppItem
import off.kys.openarcade.ui.app_picker.components.AppPickerSearchField
import off.kys.openarcade.ui.app_picker.components.AppPickerTopBar
import off.kys.openarcade.ui.components.ArcadeScrollbar
import off.kys.openarcade.ui.components.LoadingScreen
import off.kys.openarcade.ui.components.ScrollbarPad
import org.koin.androidx.compose.koinViewModel

class AppPickerScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: AppPickerViewModel = koinViewModel()
        val uiState by viewModel.uiState.collectAsState()
        val listState = rememberLazyListState()

        LaunchedEffect(uiState.isDone) {
            if (uiState.isDone) {
                navigator.pop()
            }
        }

        Scaffold(
            topBar = {
                AppPickerTopBar(
                    selectedCount = uiState.selectedPackages.size,
                    isLoading = uiState.isLoading,
                    onBackClick = { navigator.pop() },
                    onAddClick = { viewModel.onEvent(AppPickerUiEvent.AddSelectedApps) }
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (uiState.isLoading) {
                    LoadingScreen(
                        message = if (uiState.apps.isEmpty()) "Loading apps..." else "Adding apps...",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            bottom = 16.dp,
                            end = 0.dp
                        )
                    ) {
                        item(key = "search_field") {
                            AppPickerSearchField(
                                query = uiState.searchQuery,
                                onQueryChange = {
                                    viewModel.onEvent(
                                        AppPickerUiEvent.SearchQueryChanged(
                                            it
                                        )
                                    )
                                }
                            )
                        }

                        items(uiState.filteredApps, key = { it.packageName }) { app ->
                            AppItem(
                                app = app,
                                isSelected = uiState.selectedPackages.contains(app.packageName),
                                onToggle = {
                                    viewModel.onEvent(AppPickerUiEvent.ToggleAppSelection(app.packageName))
                                },
                                hapticFeedbackEnabled = uiState.hapticFeedback
                            )
                        }
                    }

                    if (uiState.showScrollbar) {
                        ArcadeScrollbar(
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
    }
}
