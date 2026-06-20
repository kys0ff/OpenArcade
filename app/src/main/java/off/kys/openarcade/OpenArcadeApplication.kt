package off.kys.openarcade

import android.app.Application
import off.kys.openarcade.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class OpenArcadeApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@OpenArcadeApplication)
            modules(appModule)
        }
    }
}
