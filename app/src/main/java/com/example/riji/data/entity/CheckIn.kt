package com.example.riji.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "check_ins")
data class CheckIn(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val icon: String = "🏃",
    val category: String = "exercise", // exercise/life
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "check_in_records",
    foreignKeys = [
        ForeignKey(
            entity = CheckIn::class,
            parentColumns = ["id"],
            childColumns = ["checkInId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["checkInId"])]
)
data class CheckInRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val checkInId: Long,
    val date: Long, // date only (start of day)
    val note: String = "",
    val photoUri: String? = null
)
