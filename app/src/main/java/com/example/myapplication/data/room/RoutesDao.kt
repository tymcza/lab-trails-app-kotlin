package com.example.myapplication.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.data.types.entities.RouteRoom

@Dao
interface RoutesDao {
    @Query("SELECT * FROM routes")
    suspend fun getAllRoutes(): List<RouteRoom>

    @Query("SELECT * FROM routes WHERE type = :routeType")
    suspend fun getRoutesByType(routeType: String): List<RouteRoom>

    @Query("SELECT * FROM routes WHERE ID = :routeID")
    suspend fun getRouteByID(routeID: String): RouteRoom?

    @Insert
    suspend fun insertAllRoutes(routesList: List<RouteRoom>)
    @Insert
    suspend fun insertRoute(routeRoom: RouteRoom)

    @Update
    suspend fun updateRoute(routeRoom: RouteRoom)

    @Delete
    suspend fun deleteRoute(routeRoom: RouteRoom)
}