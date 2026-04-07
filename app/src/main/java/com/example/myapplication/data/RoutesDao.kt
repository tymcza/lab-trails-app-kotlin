package com.example.myapplication.data

import androidx.room.*
import com.example.myapplication.data.entities.*

@Dao
interface RoutesDao {
    @Query("SELECT * FROM routes_catalogue")
    suspend fun getAllRoutes(): List<Route>

    @Query("SELECT * FROM routes_catalogue WHERE type = :routeType")
    suspend fun getRoutesByType(routeType: String): List<Route>

    @Query("SELECT * FROM routes_catalogue WHERE ID = :routeID")
    suspend fun getRouteByID(routeID: String): Route

    @Query("SELECT type_name FROM route_types ORDER BY type_name ASC")
    suspend fun getRoutesCategories(): List<String>

    @Insert
    suspend fun insertRoute(route: Route)
    @Insert
    suspend fun insertRouteType(routeType: RouteType)

    @Update
    suspend fun updateRoute(route: Route)

    @Delete
    suspend fun deleteRoute(route:Route)
}