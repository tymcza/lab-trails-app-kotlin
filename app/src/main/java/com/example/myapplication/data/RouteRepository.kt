package com.example.myapplication.data

import com.example.myapplication.data.types.RouteCommon

object RouteRepository {
    val staticRoutes = listOf(
        RouteCommon("b1", "Papa", "Biker", "4km", "Low", "Around the neighborhood."),
        RouteCommon(
            "b2",
            "Run Forest",
            "Biker",
            "8km",
            "Intermediate",
            "And he ran through the forest."
        ),
        RouteCommon("b3", "Warta", "Biker", "12km", "High", "Long, asphalt, nice open space."),

        RouteCommon("r1", "Lake District", "Runner", "15km", "Low", "Flat, relaxing."),
        RouteCommon(
            "r2",
            "Off road",
            "Runner",
            "60m",
            "Intermediate",
            "Better go with an MTB bike."
        ),
        RouteCommon(
            "r3",
            "City around",
            "Runner",
            "120km",
            "High",
            "Long, beautiful. Quite an accomplishment."
        )
    )
}