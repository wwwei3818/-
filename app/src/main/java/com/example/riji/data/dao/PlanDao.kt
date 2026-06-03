package com.example.riji.data.dao

import androidx.room.*
import com.example.riji.data.entity.Plan
import com.example.riji.data.entity.PlanTask
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDao {

    // Plan operations
    @Query("SELECT * FROM plans ORDER BY createdAt DESC")
    fun getAllPlans(): Flow<List<Plan>>

    @Query("SELECT * FROM plans WHERE category = :category ORDER BY createdAt DESC")
    fun getPlansByCategory(category: String): Flow<List<Plan>>

    @Query("SELECT * FROM plans WHERE id = :id")
    suspend fun getPlanById(id: Long): Plan?

    @Query("SELECT * FROM plans WHERE name LIKE '%' || :query || '%'")
    fun searchPlans(query: String): Flow<List<Plan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: Plan): Long

    @Update
    suspend fun updatePlan(plan: Plan)

    @Delete
    suspend fun deletePlan(plan: Plan)

    // PlanTask operations
    @Query("SELECT * FROM plan_tasks WHERE planId = :planId ORDER BY sortOrder ASC")
    fun getTasksForPlan(planId: Long): Flow<List<PlanTask>>

    @Query("SELECT * FROM plan_tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): PlanTask?

    @Query("SELECT COUNT(*) FROM plan_tasks WHERE planId = :planId")
    fun getTaskCount(planId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM plan_tasks WHERE planId = :planId AND isCompleted = 1")
    fun getCompletedTaskCount(planId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: PlanTask): Long

    @Update
    suspend fun updateTask(task: PlanTask)

    @Delete
    suspend fun deleteTask(task: PlanTask)
}
