package com.example.riji.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anniversaries")
data class Anniversary(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val date: Long, // timestamp of the original date
    val type: String = "anniversary", // birthday/anniversary/deadline/payday/rent
    val repeatType: String = "yearly", // none/yearly/monthly/weekly/daily
    val remindDaysBefore: Int = 0,
    val remindTime: String? = null, // "09:00" format
    val category: String = "life", // life/work/couple/travel
    val note: String = "",
    val icon: String = "🎂",
    val createdAt: Long = System.currentTimeMillis()
)
