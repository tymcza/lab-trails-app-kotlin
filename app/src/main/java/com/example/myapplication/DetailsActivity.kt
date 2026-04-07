package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.material3.Icon
import com.example.myapplication.data.entities.Route
import com.example.myapplication.logic.DetailsViewModel
import com.example.myapplication.logic.DetailsViewModelFactory

class DetailsActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val routeID = intent.getStringExtra("routeID") ?: "IDNotProvided"
        val mediator = (application as MyApp).dataMediator
        val viewModel: DetailsViewModel by viewModels { DetailsViewModelFactory(mediator, routeID) }

        enableEdgeToEdge()
        setContent {
            val route: Route = viewModel.route.value
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Details of ${route.name}") },
                        navigationIcon = {
                            IconButton(onClick = { finish() }){
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    )
                }
            ){ postScaffoldPadding ->
                Column(modifier = Modifier.padding(postScaffoldPadding)){
                    Text("Typ: ${route.type}\nLength: ${route.length}\nDifficulty: ${route.difficulty}\nAdditional info: ${route.additionalInfo}")
                }
            }

        }
    }
}