package com.example.myapplication.data.room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "routes",
    indices = [Index(value = ["name"], unique = true)]
)
data class RouteRoom(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String,
    val length: String,
    val difficulty: String,
    val additionalInfo: String
)