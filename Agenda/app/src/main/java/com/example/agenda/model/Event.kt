package com.example.agenda.model

import java.util.UUID

data class Event(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String?,
    val timestamp: Long,
    val color: Int,
    val isAllDay: Boolean = false
)