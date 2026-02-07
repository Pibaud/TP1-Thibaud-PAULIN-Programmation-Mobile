package com.example.trainapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainapp.data.api.Journey
import com.example.trainapp.data.api.Place
import com.example.trainapp.data.repository.TrainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TrainViewModel(private val repository: TrainRepository) : ViewModel() {

    private val _suggestions = MutableStateFlow<List<Place>>(emptyList())
    val suggestions: StateFlow<List<Place>> = _suggestions

    private val _journeys = MutableStateFlow<List<Journey>>(emptyList())
    val journeys: StateFlow<List<Journey>> = _journeys

    var lastFromId: String? = null
    var lastToId: String? = null

    fun setFromTo(fromId: String?, toId: String?) {
        lastFromId = fromId
        lastToId = toId
    }

    fun onQueryChanged(query: String) {
        if (query.length < 3) return

        viewModelScope.launch {
            try {
                val results = repository.searchPlaces(query)
                android.util.Log.d("TRAIN_APP", "Nombre de gares reçues : ${results.size}")
                _suggestions.value = results// .value car StateFlow
            } catch (e: Exception) {
                android.util.Log.e("TRAIN_APP", "ERREUR CRITIQUE DANS LE VIEWMODEL", e)
            }
        }
    }

    private var selectedDateTime: String? = null
    var selectedTimestamp: Long? = null

    fun setDateTime(isoDateTime: String?, timestamp: Long?) {
        selectedDateTime = isoDateTime
        selectedTimestamp = timestamp
    }

    fun findJourneys(fromId: String, toId: String) {
        viewModelScope.launch {
            _journeys.value = repository.getJourneys(fromId, toId, selectedDateTime)
        }
    }

    fun shiftTime(hours: Int, onDateChangeRequired: (newDateLong: Long) -> Unit) {
        val currentTimestamp = selectedTimestamp ?: System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTimestamp

        val oldDay = calendar.get(Calendar.DAY_OF_YEAR)
        calendar.add(Calendar.HOUR_OF_DAY, hours)
        val newDay = calendar.get(Calendar.DAY_OF_YEAR)

        if (oldDay != newDay) {
            onDateChangeRequired(calendar.timeInMillis)
        } else {
            updateDateTimeFromTimestamp(calendar.timeInMillis)
        }
    }

    private fun updateDateTimeFromTimestamp(timestamp: Long) {
        val sdfApi = SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.getDefault())
        val isoDate = sdfApi.format(Date(timestamp))

        setDateTime(isoDate, timestamp)

        val from = lastFromId
        val to = lastToId

        if (from != null && to != null) {
            findJourneys(from, to)
        }
    }
}