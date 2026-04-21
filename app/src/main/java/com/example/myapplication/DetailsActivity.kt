package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.RouteCommon
import com.example.myapplication.viewmodels.DetailsViewModel
import com.example.myapplication.viewmodels.DetailsViewModelFactory
import androidx.compose.runtime.getValue
import com.example.myapplication.data.RecordCommon

class DetailsActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val routeID = intent.getStringExtra("routeID") ?: "No ID provided"
        val mediator = (application as MyApp).dataMediator
        val viewModel: DetailsViewModel by viewModels { DetailsViewModelFactory(mediator, routeID) }

        enableEdgeToEdge()
        setContent {
            val route: RouteCommon? = viewModel.route.value
            val bestTime by viewModel.bestTimeFormatted.collectAsState()
            val bestDate by viewModel.bestDateFormatted.collectAsState()

            if (route == null) {
                RouteNotFoundScreen(onBack = { finish() })
            } else {
                RouteDetailsScreen(route = route, bestTime = bestTime, bestDate = bestDate, onBack = { finish() }, viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailsScreen(route: RouteCommon, bestTime: String?, bestDate: String?, onBack: () -> Unit, viewModel: DetailsViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Details of ${route.name}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                StopwatchControls(viewModel= viewModel)
            }
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {
            Text("Type: ${route.type}")
            Text("Length: ${route.length} km")
            Text("Difficulty: ${route.difficulty}")
            Text("Additional info: ${route.additionalInfo}")

            Spacer(modifier = Modifier.height(16.dp))

            if (bestTime != null) {
                Text("Your best time on this route is $bestTime, recorded on $bestDate")
            } else {
                Text("You don't have any time registered on this route yet")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteNotFoundScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Error") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Oops! RouteRoom not found.")
            Button(onClick = onBack, modifier = Modifier.padding(top = 16.dp)) {
                Text("Go Back")
            }
        }
    }
}

@Composable
fun StopwatchControls(
    modifier: Modifier = Modifier,
    viewModel: DetailsViewModel
) {
    val currentTime by viewModel.timerDisplay.collectAsState()
    val timerState by viewModel.timerState.collectAsState()

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // The zeroed display
        Text(
            text = currentTime,
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (timerState == "init") {
            FilledIconButton(
                onClick = { viewModel.startTimer() },
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start Timer",
                    modifier = Modifier.size(32.dp)
                )
            }
        } else if (timerState == "play") {
            FilledIconButton(
                onClick = { viewModel.pauseTimer() },
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Pause Timer",
                    modifier = Modifier.size(32.dp)
                )
            }
        } else if (timerState == "pause") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Save Button
                FilledIconButton(
                    onClick = { viewModel.saveTimer() },
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check, // Or Icons.Default.Replay
                        contentDescription = "Save Your Time Record",
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Resume Button
                FilledIconButton(
                    onClick = { viewModel.startTimer() },
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Resume Timer",
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Discard Button
                FilledIconButton(
                    onClick = { viewModel.deleteTimer() },
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete, // Or Icons.Default.Replay
                        contentDescription = "Delete Your Time Record",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}