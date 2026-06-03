package com.example.riji.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "plans")
data class Plan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String = "life", // life/work/travel/couple
    val icon: String = "📋",
    val manualProgress: Int = -1, // -1 means auto-calculate from tasks, 0-100 means manual
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "plan_tasks",
    foreignKeys = [
        ForeignKey(
            entity = Plan::class,
            parentColumns = ["id"],
            childColumns = ["planId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["planId"])]
)
data class PlanTask(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val planId: Long,
    val title: String,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val sortOrder: Int = 0
)
