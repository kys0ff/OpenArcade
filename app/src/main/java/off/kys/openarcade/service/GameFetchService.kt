package off.kys.openarcade.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import off.kys.openarcade.data.local.AppDatabase
import off.kys.openarcade.data.repository.GameRepositoryImpl

class GameFetchService : Service() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "openarcade-db"
        ).build()
        
        val repository = GameRepositoryImpl(applicationContext, database.gameDao())

        serviceScope.launch {
            repository.refreshGames()
            stopSelf()
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
}
