package com.example.myapplication.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.DataMediator

class MainViewModel(private val mediator: DataMediator) : ViewModel() {

    var selectedCategory: MutableState<String?> = mutableStateOf(null)
        private set
    val categories = mediator.getRoutesCategoriesFlow()
    val displayedRoutesList = mediator.getRoutesByTypeFlow()

    fun updateCategory(category: String) {
        selectedCategory.value = category
        mediator.fetchRoutesByType(category)
        mediator.fetchRoutesCategories()
    }
}

class MainViewModelFactory(private val mediator: DataMediator) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(mediator) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}