package com.example.myapplication.data

import com.example.myapplication.data.RouteRepository.staticRoutes
import com.example.myapplication.data.entities.Route
import com.example.myapplication.data.dto.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class DataMediator(private val dao: RoutesDao){

    @Volatile
    private var databaseInit = false
    private var databaseRoutes: List<Route> = listOf()

    private val mediatorScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    fun initializeDataBase() {
        mediatorScope.launch {
            try{
                val result = dao.getAllRoutes()
                databaseRoutes = result
                databaseInit = true
            } catch (e: Exception) {
                databaseInit = false
            }
        }
    }

    val allRoutes: List<Route>
        get() = if (databaseInit && databaseRoutes.isNotEmpty()) databaseRoutes else staticRoutes

    fun getRoutesByType(type: String): List<Route> {
        return allRoutes.filter { it.type == type }
    }

    fun getRouteById(id: Long): Route? {
        return allRoutes.find { it.id == id }
    }

    fun getRoutesCategories(): List<String> {
        return allRoutes.map { it.type }.distinct().sorted()
    }

    fun dtoToRoomEntity(response: WarszawaApiResponseDto): List<Route> {
        val routeList = mutableListOf<Route>()

        for (dto in response.result) {
            val numericLength = dto.length.toDoubleOrNull() ?: 0.0

            val difficultyLabel = when {
                numericLength <= 0.0 -> "No data"
                numericLength < 6.0 -> "Low"
                numericLength < 15.0 -> "Intermediate"
                else -> "High"
            }

            val roomEntity = Route(
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