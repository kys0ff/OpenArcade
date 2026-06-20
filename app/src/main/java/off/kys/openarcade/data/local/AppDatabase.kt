package off.kys.openarcade.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import off.kys.openarcade.data.local.dao.GameDao
import off.kys.openarcade.domain.model.GameEntry

@Database(entities = [GameEntry::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
}
