package com.example.myapplication.data

import com.example.myapplication.Secrets
import com.example.myapplication.data.RouteRepository.staticRoutes
import com.example.myapplication.data.retrofit.WarsawApiService
import com.example.myapplication.data.room.RoutesDao
import com.example.myapplication.data.types.entities.RouteRoom
import com.example.myapplication.data.types.RouteCommon
import com.example.myapplication.data.types.dto.WarsawApiResponseDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class DataMediator(private val dao: RoutesDao, private val warsawApiService: WarsawApiService){

    @Volatile
    private var databaseInit = false
    private var databaseRoutes: List<RouteCommon> = listOf()

    private val mediatorScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    suspend fun loadFromDataBase() {
        try{
            val result = dao.getAllRoutes()
            databaseRoutes = result.map { entity -> routeRoomEntityToCommon(entity) }
            databaseInit = true
        } catch (e: Exception) {
            databaseInit = false
            e.printStackTrace()
        }
    }

    fun fetchApiRefreshDb() {
        mediatorScope.launch {
            try {
                val warsawApiResponseDto = warsawApiService.getTouristRoutes(apiKey = Secrets.API_KEY)
                val roomEntities = routeDtoToRoomEntity(warsawApiResponseDto)
                dao.insertAllRoutes(roomEntities)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            loadFromDataBase()
        }
    }

    val allRouteCommons: List<RouteCommon>
        get() = if (databaseInit && databaseRoutes.isNotEmpty()) databaseRoutes else staticRoutes

    fun getRoutesByType(type: String): List<RouteCommon> {
        return allRouteCommons.filter { it.type == type }
    }

    fun getRouteById(id: String): RouteCommon? {
        return allRouteCommons.find { it.id == id }
    }

    fun getRoutesCategories(): List<String> {
        return allRouteCommons.map { it.type }.distinct().sorted()
    }

    fun routeRoomEntityToCommon(entity: RouteRoom): RouteCommon {
        return RouteCommon(
            entity.id.toString(),
            name = entity.name,
            type = entity.type,
            length = entity.length,
            difficulty = entity.difficulty,
            additionalInfo = entity.additionalInfo
        )
    }

    fun routeDtoToRoomEntity(response: WarsawApiResponseDto): List<RouteRoom> {
        val routeList = mutableListOf<RouteRoom>()

        for (dto in response.result) {
            val numericLength = dto.length.toDoubleOrNull() ?: 0.0

            val difficultyLabel = when {
                numericLength <= 0.0 -> "No data"
                numericLength < 6.0 -> "Low"
                numericLength < 15.0 -> "Intermediate"
                else -> "High"
            }

            val roomEntity = RouteRoom(
                id = 0,
                name = dto.title,
                type = "tourist",
                length = dto.length,
                difficulty = difficultyLabel,
                additionalInfo = dto.description
            )

            routeList.add(roomEntity)
        }
        return routeList
    }

}