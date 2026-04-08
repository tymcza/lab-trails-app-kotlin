package com.example.myapplication.logic

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.DataMediator

class DetailsViewModel(private val mediator: DataMediator, private val routeID: Long) : ViewModel() {
    val route = mutableStateOf(mediator.getRouteById(routeID))

    fun refresh() {
        route.value = mediator.getRouteById(routeID)
    }
}


class DetailsViewModelFactory( private val mediator: DataMediator, private val routeId: Long ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailsViewModel(mediator, routeId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}