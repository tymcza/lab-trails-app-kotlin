package com.example.myapplication.data.entities

import androidx.room.*

@Entity(tableName = "routes_catalogue")
data class Route(
    @PrimaryKey (autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String,
    val length: String,
    val difficulty: String,
    val additionalInfo: String
)