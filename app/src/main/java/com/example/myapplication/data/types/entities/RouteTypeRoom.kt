package com.example.myapplication.data.types.entities

import androidx.room.*

@Entity(tableName = "route_types")
data class RouteTypeRoom(
    @PrimaryKey val id: String,

    @ColumnInfo(name = "type_name")
    val routeType: String
)