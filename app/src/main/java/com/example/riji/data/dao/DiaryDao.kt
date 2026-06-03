package com.example.riji.data.dao

import androidx.room.*
import com.example.riji.data.entity.Diary
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {

    @Query("SELECT * FROM diaries ORDER BY createdAt DESC")
    fun getAllDiaries(): Flow<List<Diary>>

    @Query("SELECT * FROM diaries WHERE id = :id")
    suspend fun getDiaryById(id: Long): Diary?

    @Query("SELECT * FROM diaries WHERE tags LIKE '%' || :tag || '%' ORDER BY createdAt DESC")
    fun getDiariesByTag(tag: String): Flow<List<Diary>>

    @Query("SELECT * FROM diaries WHERE content LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchDiaries(query: String): Flow<List<Diary>>

    @Query("SELECT * FROM diaries WHERE createdAt >= :startDate AND createdAt <= :endDate ORDER BY createdAt DESC")
    fun getDiariesBetweenDates(startDate: Long, endDate: Long): Flow<List<Diary>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiary(diary: Diary): Long

    @Update
    suspend fun updateDiary(diary: Diary)

    @Delete
    suspend fun deleteDiary(diary: Diary)

    @Query("DELETE FROM diaries WHERE id = :id")
    suspend fun deleteDiaryById(id: Long)
}
