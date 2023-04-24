package com.example.project1
import android.content.Context
import androidx.room.Database
import kotlin.jvm.Volatile
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [UserTable::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    // Make the db singleton. Could in theory
    // make this an object class, but the companion object approach
    // is nicer (imo)
    companion object {
        @Volatile
        private var mInstance: AppDatabase? = null
        fun getDatabase(
            context: Context,
            scope : CoroutineScope
        ): AppDatabase {
            return mInstance?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "app.db"
                )
                    .addCallback(RoomDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
                mInstance = instance
                instance
            }
        }

        private class RoomDatabaseCallback(
            private val scope: CoroutineScope
        ): RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                mInstance?.let { database ->
                    scope.launch(Dispatchers.IO){
                        //populateDbTask(database.appDao())
                    }
                }
            }
        }

//        suspend fun populateDbTask (weatherDao: AppDao) {
//            weatherDao.insert(UserTable("Dummy_loc","Dummy_data"))
//        }
    }
}