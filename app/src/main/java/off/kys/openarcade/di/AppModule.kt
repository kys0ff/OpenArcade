package off.kys.openarcade.di

import androidx.room.Room
import off.kys.openarcade.data.local.AppDatabase
import off.kys.openarcade.data.repository.GameRepositoryImpl
import off.kys.openarcade.domain.repository.GameRepository
import off.kys.openarcade.domain.usecase.GetGameByPackageUseCase
import off.kys.openarcade.domain.usecase.GetGamesUseCase
import off.kys.openarcade.domain.usecase.RefreshGamesUseCase
import off.kys.openarcade.domain.usecase.UpdateGameCategoryUseCase
import off.kys.openarcade.ui.detail.GameDetailViewModel
import off.kys.openarcade.ui.launcher.GamesLauncherViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java, "openarcade-db"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    single { get<AppDatabase>().gameDao() }

    single<GameRepository> { GameRepositoryImpl(androidContext(), get()) }

    factory { GetGamesUseCase(get()) }
    factory { RefreshGamesUseCase(get()) }
    factory { GetGameByPackageUseCase(get()) }
    factory { UpdateGameCategoryUseCase(get()) }

    viewModel { GamesLauncherViewModel(
        application = androidApplication(),
        refreshGamesUseCase = get(),
        getGamesUseCase = get()
    ) }

    viewModel { (packageName: String) ->
        GameDetailViewModel(
            packageName = packageName,
            application = androidApplication(),
            getGameByPackageUseCase = get(),
            updateGameCategoryUseCase = get()
        )
    }
}
