package com.example.myapplication.data

import com.example.myapplication.data.entities.Route

object RouteRepository {
    val staticRoutes = listOf(
        Route("b1", "Papa", "Biker", "4km", "Low", "Around the neighborhood."),
        Route("b2", "Run Forest", "Biker", "8km", "Intermediate", "And he ran through the forest."),
        Route("b3", "Warta", "Biker", "12km", "High", "Long, asphalt, nice open space."),

        Route("r1", "Lake District", "Runner", "15km", "Low", "Flat, relaxing."),
        Route("r2", "Off road", "Runner", "60m", "Intermediate", "Better go with an MTB bike."),
        Route("r3", "City around", "Runner", "120km", "High", "Long, beautiful. Quite an accomplishment.")
    )
}