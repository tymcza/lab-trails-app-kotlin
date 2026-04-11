package com.example.myapplication.data

import androidx.room.*
import com.example.myapplication.data.types.entities.RouteRoom
import com.example.myapplication.data.types.entities.RouteTypeRoom

@Dao
interface RoutesDao {
    @Query("SELECT * FROM routes_catalogue")
    suspend fun getAllRoutes(): List<RouteRoom>

    @Query("SELECT * FROM routes_catalogue WHERE type = :routeType")
    suspend fun getRoutesByType(routeType: String): List<RouteRoom>

    @Query("SELECT * FROM routes_catalogue WHERE ID = :routeID")
    suspend fun getRouteByID(routeID: String): RouteRoom?

    @Query("SELECT type_name FROM route_types ORDER BY type_name ASC")
    suspend fun getRoutesCategories(): List<String>

    @Insert
    suspend fun insertRoute(routeRoom: RouteRoom)
    @Insert
    suspend fun insertRouteType(routeTypeRoom: RouteTypeRoom)

    @Update
    suspend fun updateRoute(routeRoom: RouteRoom)

    @Delete
    suspend fun deleteRoute(routeRoom: RouteRoom)
}