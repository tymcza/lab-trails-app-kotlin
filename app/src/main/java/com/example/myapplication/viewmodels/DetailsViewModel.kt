package com.example.myapplication.viewmodels


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.DataMediator
import com.example.myapplication.data.RecordCommon
import com.example.myapplication.data.RouteCommon
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class DetailsViewModel(private val mediator: DataMediator, private val routeID: String) : ViewModel() {

    var route by mutableStateOf<RouteCommon?>(null)
        private set

    init {
        viewModelScope.launch {
            route = mediator.getRouteById(routeID)
            _bestTimeRecord.value = mediator.getBestRecordById(routeID).firstOrNull()
        }
    }

    private val _bestTimeRecord = MutableStateFlow<RecordCommon?>(null)

    val bestDateFormatted: StateFlow<String?> = _bestTimeRecord.map { record ->
        record?.let {
            java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                .format(java.util.Date(it.date*1000))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val bestTimeFormatted: StateFlow<String?> = _bestTimeRecord.map { record ->
        record?.let { formatSeconds(it.registeredTimeSeconds) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun refresh() {
        viewModelScope.launch {
            _bestTimeRecord.value = mediator.getBestRecordById(routeID).firstOrNull()
        }
    }

    private val _timerState = MutableStateFlow(TimerState.ZERO)
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()
    private var timerJob: Job? = null
    var totalSeconds: Long = 0
    private val _timerDisplay = MutableStateFlow("00:00:00")
    val timerDisplay: StateFlow<String> = _timerDisplay.asStateFlow()

    fun startTimer() {
        _timerState.value = TimerState.PLAYING
        if (timerJob?.isActive == true) return

        timerJob = viewModelScope.launch {
            while (_timerState.value == TimerState.PLAYING) {
                delay(1000)
                if (_timerState.value != TimerState.PLAYING) {
                    break
                }
                totalSeconds++

                val hours = totalSeconds / 3600
                val minutes = (totalSeconds % 3600) / 60
                val seconds = totalSeconds % 60

                _timerDisplay.value = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            }
        }
    }

    fun pauseTimer() {
        _timerState.value = TimerState.PAUSE
    }

    fun deleteTimer() {
        _timerState.value = TimerState.ZERO
        timerJob = null
        totalSeconds = 0
        _timerDisplay.value = "00:00:00"
    }

    fun saveTimer() {
        _timerState.value = TimerState.ZERO
        timerJob = null

        val todayMidnightSeconds = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toEpochSecond()
        val record = RecordCommon(
            id = "0",
            correspondingRouteId = routeID,
            registeredTimeSeconds = totalSeconds,
            date = todayMidnightSeconds
        )

        totalSeconds = 0
        _timerDisplay.value = "00:00:00"

        viewModelScope.launch {
            mediator.saveRecord(record)
            refresh()
        }
    }

    private fun formatSeconds(totalSeconds: Long): String {
        val h = totalSeconds / 3600
        val m = (totalSeconds % 3600) / 60
        val s = totalSeconds % 60

        return buildString {
            if (h > 0) append("${h}h ")
            if (m > 0 || h > 0) append("${m}m ") // Shows 0m if there are hours (e.g., 1h 0m 5s)
            append("${s}s")
        }.trim()
    }
}


class DetailsViewModelFactory( private val mediator: DataMediator, private val routeId: String ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailsViewModel(mediator, routeId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}