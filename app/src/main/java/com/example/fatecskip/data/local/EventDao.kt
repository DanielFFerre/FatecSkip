package com.example.fatecskip.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fatecskip.data.local.entities.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM academic_events ORDER BY date ASC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM academic_events WHERE date = :date")
    suspend fun getEventsByDate(date: String): List<EventEntity>

    @Query("SELECT * FROM academic_events WHERE date = :date AND isCritical = 1")
    suspend fun getCriticalEventsByDate(date: String): List<EventEntity>

    @Query("SELECT * FROM academic_events WHERE date = :date AND type = 'HOLIDAY'")
    suspend fun getHolidaysByDate(date: String): List<EventEntity>

    @Query("SELECT * FROM academic_events WHERE date >= :startDate ORDER BY date ASC LIMIT :limit")
    suspend fun getUpcomingEvents(startDate: String, limit: Int = 10): List<EventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<EventEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity): Long

    @Delete
    suspend fun deleteEvent(event: EventEntity)

    @Query("DELETE FROM academic_events WHERE id = :id")
    suspend fun deleteEventById(id: Long)

    @Query("DELETE FROM academic_events WHERE type = 'HOLIDAY'")
    suspend fun clearHolidays()

    @Query("DELETE FROM academic_events")
    suspend fun clearAllEvents()

    @Query("SELECT COUNT(*) FROM academic_events")
    suspend fun getEventCount(): Int
}

