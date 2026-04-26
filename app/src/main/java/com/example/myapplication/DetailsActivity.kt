package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.RouteCommon
import com.example.myapplication.viewmodels.DetailsViewModel
import com.example.myapplication.viewmodels.DetailsViewModelFactory
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.myapplication.viewmodels.TimerState

class DetailsActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val routeID = intent.getStringExtra("routeID") ?: "No ID provided"
        val mediator = (application as MyApp).dataMediator
        val viewModel: DetailsViewModel by viewModels { DetailsViewModelFactory(mediator, routeID) }

        enableEdgeToEdge()
        setContent {
            val route: RouteCommon? = viewModel.route
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopwatchControls(
    modifier: Modifier = Modifier,
    viewModel: DetailsViewModel
) {
    val currentTime by viewModel.timerDisplay.collectAsState()
    val timerState by viewModel.timerState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Discard record") },
            text = { Text("Are you sure you want to discard your time record?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteTimer()
                        showDialog = false
                    }
                ) {
                    Text("Discard")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when (timerState) {
            TimerState.ZERO -> {}
            TimerState.PLAYING, TimerState.PAUSE -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    OutlinedIconButton(
                        onClick = { showDialog = true },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 32.dp)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Discard",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                    Text(
                        text = currentTime,
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (timerState == TimerState.PAUSE) {
                        OutlinedIconButton(
                            onClick = { viewModel.saveTimer() },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 32.dp)
                                .size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Save your time record",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = {
                when (timerState) {
                    TimerState.ZERO -> viewModel.startTimer()
                    TimerState.PLAYING -> viewModel.pauseTimer()
                    TimerState.PAUSE -> viewModel.startTimer()
                }
            },
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(72.dp)
        ) {
            Icon(
                imageVector = when (timerState) {
                    TimerState.ZERO -> Icons.Default.PlayArrow
                    TimerState.PLAYING -> Icons.Default.Pause
                    TimerState.PAUSE -> Icons.Default.PlayArrow
                },
                contentDescription = when (timerState) {
                    TimerState.ZERO -> "Start Timer"
                    TimerState.PLAYING -> "Pause Timer"
                    TimerState.PAUSE -> "Resume Timer"
                },
                modifier = Modifier.size(36.dp)
            )
        }

    }
}