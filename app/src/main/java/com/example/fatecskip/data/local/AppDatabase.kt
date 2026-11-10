package com.example.fatecskip.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.fatecskip.data.local.entities.EventEntity
import com.example.fatecskip.data.local.entities.ExcuseEntity
import com.example.fatecskip.data.local.entities.SubjectEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        SubjectEntity::class,
        EventEntity::class,
        ExcuseEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun subjectDao(): SubjectDao
    abstract fun eventDao(): EventDao
    abstract fun excuseDao(): ExcuseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fatec_skip_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Popula o banco com a grade oficial do 1º Semestre de 2026 de DSM
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    database.subjectDao().insertSubjects(DefaultFatecData.getDefaultSubjects())
                                    database.eventDao().insertEvents(DefaultFatecData.getDefaultHolidays())
                                    database.excuseDao().insertExcuses(DefaultFatecData.getDefaultExcuses())
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
