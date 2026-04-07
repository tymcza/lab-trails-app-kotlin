package com.example.myapplication.data

import com.example.myapplication.data.RouteRepository.staticRoutes
import com.example.myapplication.data.entities.Route
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class DataMediator(private val dao: RoutesDao){

    @Volatile
    private var databaseInit = false
    private var databaseRoutes: List<Route> = listOf()
    val nullRoute = Route("err", "ThisIsANullRoute", "Null", "Null", "Null", "Null")



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

    fun getRouteById(id: String): Route {
        return allRoutes.find { it.id == id } ?: nullRoute
    }

    fun getRoutesCategories(): List<String> {
        return allRoutes.map { it.type }.distinct().sorted()
    }

}