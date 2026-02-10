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
import java.util.TimeZone

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

    var selectedDateTime: String? = null
    var selectedTimestamp: Long? = null

    // Ajoute ces variables dans ton ViewModel
    var selectedHour: Int = 6 // Par défaut 6h
    var selectedMinute: Int = 0

    // Fonction pour mettre à jour l'heure uniquement
    fun setTime(hour: Int, minute: Int) {
        selectedHour = hour
        selectedMinute = minute

        // Si on a déjà une date sélectionnée, on met à jour le datetime complet
        selectedTimestamp?.let { timestamp ->
            updateDateTimeFromComponents(timestamp, hour, minute)
        }
    }

    fun updateDateTimeFromComponents(dateTimestamp: Long, hour: Int, minute: Int) {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.timeInMillis = dateTimestamp

        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)

        val finalTimestamp = calendar.timeInMillis

        val sdf = SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.getDefault())
        val isoDate = sdf.format(Date(finalTimestamp))

        setDateTime(isoDate, finalTimestamp)

        val from = lastFromId
        val to = lastToId
        if (from != null && to != null) {
            findJourneys(from, to)
        }
    }

    fun setDateTime(isoDateTime: String?, timestamp: Long?) {
        selectedDateTime = isoDateTime
        selectedTimestamp = timestamp
    }

    private val allJourneys = mutableListOf<Journey>()

    fun findJourneys(fromId: String, toId: String) {
        viewModelScope.launch {
            val newResults = repository.getJourneys(fromId, toId, selectedDateTime)

            allJourneys.addAll(newResults)

            val uniqueSortedJourneys = allJourneys
                .distinctBy { it.departureDateTime }
                .sortedBy { it.departureDateTime }

            _journeys.value = uniqueSortedJourneys
        }
    }

    fun clearHistory() {
        allJourneys.clear()
        _journeys.value = emptyList()
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