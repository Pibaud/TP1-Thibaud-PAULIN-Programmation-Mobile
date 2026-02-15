package com.example.agenda.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import java.util.Calendar
import androidx.lifecycle.viewModelScope
import com.example.agenda.model.Event
import com.example.agenda.model.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventViewModel(private val repository: EventRepository) : ViewModel() {

    private val _allEvents = MutableStateFlow<List<Event>>(emptyList())

    private val _selectedDate = MutableStateFlow(System.currentTimeMillis())

    val events: StateFlow<List<Event>> = combine(_allEvents, _selectedDate) { list, dateTimestamp ->
        list.filter { event ->
            isSameDay(event.timestamp, dateTimestamp)
        }.sortedWith(
            compareByDescending<Event> { it.isAllDay }
                .thenBy { it.timestamp }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadAllEvents()
    }

    private fun isSameDay(date1: Long, date2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = date1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun loadAllEvents() {
        viewModelScope.launch {
            _allEvents.value = repository.loadEvents()
        }
    }

    fun selectDate(timestamp: Long) {
        _selectedDate.value = timestamp
    }

    fun addEvent(newEvent: Event) {
        viewModelScope.launch {
            val currentList = _allEvents.value.toMutableList()
            currentList.add(newEvent)
            repository.saveEvents(currentList)
            _allEvents.value = currentList
        }
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            val updatedList = _allEvents.value.filter { it.id != eventId }
            _allEvents.value = updatedList
            repository.saveEvents(updatedList)
        }
    }
}