package com.example.myapplication.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.DataMediator

class MainViewModel(private val mediator: DataMediator) : ViewModel() {

    val categories = mediator.fetchRoutesCategories()
    var selectedCategory: MutableState<String?> = mutableStateOf(null)
        private set

    //TODO: Update to Flow
    val displayedRoutesList = mediator.fetchRoutesByType(selectedCategory.value)

    fun updateCategory(category: String) {
        selectedCategory.value = category
        mediator.fetchRoutesByType(category)
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