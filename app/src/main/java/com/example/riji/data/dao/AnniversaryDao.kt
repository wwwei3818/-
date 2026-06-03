package com.example.riji.data.dao

import androidx.room.*
import com.example.riji.data.entity.Anniversary
import kotlinx.coroutines.flow.Flow

@Dao
interface AnniversaryDao {

    @Query("SELECT * FROM anniversaries ORDER BY date ASC")
    fun getAllAnniversaries(): Flow<List<Anniversary>>

    @Query("SELECT * FROM anniversaries WHERE category = :category ORDER BY date ASC")
    fun getAnniversariesByCategory(category: String): Flow<List<Anniversary>>

    @Query("SELECT * FROM anniversaries WHERE id = :id")
    suspend fun getAnniversaryById(id: Long): Anniversary?

    @Query("SELECT * FROM anniversaries WHERE name LIKE '%' || :query || '%' OR note LIKE '%' || :query || '%'")
    fun searchAnniversaries(query: String): Flow<List<Anniversary>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnniversary(anniversary: Anniversary): Long

    @Update
    suspend fun updateAnniversary(anniversary: Anniversary)

    @Delete
    suspend fun deleteAnniversary(anniversary: Anniversary)

    @Query("DELETE FROM anniversaries WHERE id = :id")
    suspend fun deleteAnniversaryById(id: Long)
}
