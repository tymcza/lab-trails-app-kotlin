package com.example.myapplication.data

object RouteRepository {
    val nullRoute = Route("err", "ThisIsANullRoute", "Null", "Null", "Null", "Null")

    val allRoutes = listOf(
        Route("b1", "Neighbourhood", "Biker", "4km", "Low", "Around the neighbourhood."),
        Route("b2", "Run Forest", "Biker", "8km", "Intermediate", "And he ran through the forest."),
        Route("b3", "Warta", "Biker", "12km", "High", "Long, asphalt, nice open space."),

        Route("r1", "Lake District", "Runner", "15km", "Low", "Flat, relaxing."),
        Route("r2", "Off road", "Runner", "60m", "Intermediate", "Better go with an MTB bike."),
        Route("r3", "City around", "Runner", "120km", "High", "Long, beautiful. Quite an accomplishment.")
    )

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