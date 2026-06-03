package com.example.riji.data.dao

import androidx.room.*
import com.example.riji.data.entity.CheckIn
import com.example.riji.data.entity.CheckInRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckInDao {

    // CheckIn operations
    @Query("SELECT * FROM check_ins ORDER BY createdAt DESC")
    fun getAllCheckIns(): Flow<List<CheckIn>>

    @Query("SELECT * FROM check_ins WHERE category = :category ORDER BY createdAt DESC")
    fun getCheckInsByCategory(category: String): Flow<List<CheckIn>>

    @Query("SELECT * FROM check_ins WHERE id = :id")
    suspend fun getCheckInById(id: Long): CheckIn?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckIn(checkIn: CheckIn): Long

    @Update
    suspend fun updateCheckIn(checkIn: CheckIn)

    @Delete
    suspend fun deleteCheckIn(checkIn: CheckIn)

    // CheckInRecord operations
    @Query("SELECT * FROM check_in_records WHERE checkInId = :checkInId ORDER BY date DESC")
    fun getRecordsForCheckIn(checkInId: Long): Flow<List<CheckInRecord>>

    @Query("SELECT * FROM check_in_records WHERE checkInId = :checkInId AND date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getRecordsBetweenDates(checkInId: Long, startDate: Long, endDate: Long): Flow<List<CheckInRecord>>

    @Query("SELECT COUNT(*) FROM check_in_records WHERE checkInId = :checkInId")
    fun getRecordCount(checkInId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM check_in_records WHERE checkInId = :checkInId AND date >= :startDate AND date <= :endDate")
    fun getRecordCountBetweenDates(checkInId: Long, startDate: Long, endDate: Long): Flow<Int>

    @Query("SELECT MAX(date) FROM check_in_records WHERE checkInId = :checkInId")
    fun getLastCheckInDate(checkInId: Long): Flow<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: CheckInRecord): Long

    @Delete
    suspend fun deleteRecord(record: CheckInRecord)

    @Query("SELECT EXISTS(SELECT 1 FROM check_in_records WHERE checkInId = :checkInId AND date = :date)")
    suspend fun hasRecordOnDate(checkInId: Long, date: Long): Boolean
}
