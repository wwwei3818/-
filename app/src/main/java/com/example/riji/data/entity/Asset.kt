package com.example.riji.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assets")
data class Asset(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val price: Double,
    val purchaseDate: Long,
    val category: String = "electronics", // electronics/appliance/clothing/daily
    val icon: String = "📱",
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
