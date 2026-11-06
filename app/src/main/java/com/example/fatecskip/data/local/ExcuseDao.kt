package com.example.fatecskip.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fatecskip.data.local.entities.ExcuseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExcuseDao {
    @Query("SELECT * FROM excuses")
    fun getAllExcuses(): Flow<List<ExcuseEntity>>

    @Query("SELECT * FROM excuses ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomExcuse(): ExcuseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExcuses(excuses: List<ExcuseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExcuse(excuse: ExcuseEntity): Long

    @Query("DELETE FROM excuses")
    suspend fun clearAllExcuses()

    @Query("SELECT COUNT(*) FROM excuses")
    suspend fun getExcuseCount(): Int
}

