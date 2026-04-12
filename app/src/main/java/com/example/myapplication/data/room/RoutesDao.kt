package com.example.myapplication.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.data.room.RouteRoom

@Dao
interface RoutesDao {
    @Query("SELECT * FROM routes ORDER BY name ASC")
    suspend fun getAllRoutes(): List<RouteRoom>

    @Query("SELECT * FROM routes WHERE type = :routeType ORDER BY name ASC")
    suspend fun getRoutesByType(routeType: String): List<RouteRoom>

    @Query("SELECT * FROM routes WHERE ID = :routeID")
    suspend fun getRouteByID(routeID: String): RouteRoom?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRoutes(routesList: List<RouteRoom>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(routeRoom: RouteRoom)

    @Update
    suspend fun updateRoute(routeRoom: RouteRoom)

    @Delete
    suspend fun deleteRoute(routeRoom: RouteRoom)
}