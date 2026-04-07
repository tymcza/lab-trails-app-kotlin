package com.example.myapplication.data.entities

import androidx.room.*

@Entity(tableName = "routes_catalogue")
data class Route(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,
    val length: String,
    val difficulty: String,
    val additionalInfo: String
)