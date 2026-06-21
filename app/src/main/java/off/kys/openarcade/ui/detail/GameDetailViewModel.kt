package off.kys.openarcade.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import off.kys.openarcade.domain.model.GameEntry
import off.kys.openarcade.domain.usecase.GetGameByPackageUseCase
import off.kys.openarcade.domain.usecase.UpdateGameCategoryUseCase

class GameDetailViewModel(
    private val packageName: String,
    application: Application,
    getGameByPackageUseCase: GetGameByPackageUseCase,
    private val updateGameCategoryUseCase: UpdateGameCategoryUseCase
) : AndroidViewModel(application) {

    val gameState: StateFlow<GameEntry?> = getGameByPackageUseCase(packageName)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun launchGame() {
        val launchIntent = application.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            application.startActivity(launchIntent)
        } else {
            // Log error or handle failure
        }
    }

    fun updateCategories(categories: List<String>) {
        viewModelScope.launch {
            updateGameCategoryUseCase(packageName, categories)
        }
    }
}
