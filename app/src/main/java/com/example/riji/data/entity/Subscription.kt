package com.example.riji.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscriptions")
data class Subscription(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val price: Double,
    val cycle: String = "monthly", // monthly/yearly/weekly
    val startDate: Long,
    val isActive: Boolean = true,
    val icon: String = "🎵",
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
