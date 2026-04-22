package com.example.myapplication.data

object RouteRepository {
    val staticRoutes = listOf(
        RouteCommon("1",
            "Papa",
            "Biker",
            "4km",
            "Low",
            "Around the neighborhood."),
        RouteCommon(
            "2",
            "Run Forest",
            "Biker",
            "8km",
            "Intermediate",
            "And he ran through the forest."
        ),
        RouteCommon("3",
            "Warta",
            "Biker",
            "12km",
            "High",
            "Long, asphalt, nice open space."),

        RouteCommon("4",
            "Lake District",
            "Runner",
            "15km",
            "Low",
            "Flat, relaxing."),
        RouteCommon(
            "5",
            "Off road",
            "Runner",
            "60m",
            "Intermediate",
            "Better go with an MTB bike."
        ),
        RouteCommon(
            "6",
            "City around",
            "Runner",
            "120km",
            "High",
            "Long, beautiful. Quite an accomplishment."
        )
    )

    fun getRouteTypes(): List<String> {
        return staticRoutes.map { it.type }.distinct()
    }

    fun getRoutesByType(type: String): List<RouteCommon> {
        return staticRoutes.filter { it.type == type }
    }
}