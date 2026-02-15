package com.example.agenda.model

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class EventRepository(context: Context) {
    private val prefs = context.getSharedPreferences("agenda_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveEvents(events: List<Event>) {
        val json = gson.toJson(events)
        prefs.edit { putString("events_list", json) }
    }

    fun loadEvents(): List<Event> {
        val json = prefs.getString("events_list", null) ?: return emptyList()
        val type = object : TypeToken<List<Event>>() {}.type
        return gson.fromJson(json, type)
    }
}