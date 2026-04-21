package com.example.myapplication.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert

@Dao
interface RoutesDao {
    @Query("SELECT * FROM routes ORDER BY name ASC")
    suspend fun getAllRoutes(): List<RouteRoom>

    @Query("SELECT * FROM records WHERE correspondingRouteId = :routeId ORDER BY registeredTimeSeconds ASC LIMIT 1")
    suspend fun getBestRecordByRouteId(routeId: Long): List<RecordRoom>

    @Upsert
    suspend fun insertAllRoutes(routesList: List<RouteRoom>)
    @Insert
    suspend fun insertRoute(routeRoom: RouteRoom)
    @Insert
    suspend fun insertRecord(recordRoom: RecordRoom)

    @Update
    suspend fun updateRoute(routeRoom: RouteRoom)

    @Delete
    suspend fun deleteRoute(routeRoom: RouteRoom)
}