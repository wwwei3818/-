package com.example.riji.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diaries")
data class Diary(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val content: String,
    val tags: String = "", // comma-separated: "日常,工作,生活"
    val images: String = "", // comma-separated image URIs
    val createdAt: Long = System.currentTimeMillis()
)
