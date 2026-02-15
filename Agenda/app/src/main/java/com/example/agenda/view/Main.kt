package com.example.agenda.view

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.view.View
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.agenda.R
import com.example.agenda.model.EventRepository
import com.example.agenda.viewmodel.EventViewModel
import kotlinx.coroutines.launch
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.agenda.model.Event
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Calendar

class Main : AppCompatActivity() {
    private lateinit var viewModel: EventViewModel
    private lateinit var eventAdapter: EventAdapter

    private var currentSelectedDate: Long = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        findViewById<FloatingActionButton>(R.id.fab_add_event).setOnClickListener {
            showAddEventDialog()
        }
        val tvEmptyMessage = findViewById<TextView>(R.id.tv_empty_message)
        val rvEvents = findViewById<RecyclerView>(R.id.rv_events)
        val calendarView = findViewById<android.widget.CalendarView>(R.id.calendarView)

        val repository = EventRepository(this)
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return EventViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[EventViewModel::class.java]

        eventAdapter = EventAdapter { eventToDelete ->
            showDeleteConfirmationDialog(eventToDelete)
        }

        rvEvents.adapter = eventAdapter
        rvEvents.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { agendaItems ->
                    eventAdapter.submitList(agendaItems)

                    if (agendaItems.isEmpty()) {
                        tvEmptyMessage.visibility = View.VISIBLE
                        rvEvents.visibility = View.GONE
                    } else {
                        tvEmptyMessage.visibility = View.GONE
                        rvEvents.visibility = View.VISIBLE
                    }
                }
            }
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)

            currentSelectedDate = calendar.timeInMillis

            viewModel.selectDate(currentSelectedDate)
        }
    }

    private fun showAddEventDialog() {
        val nowCalendar = Calendar.getInstance()
        val currentHour = nowCalendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = nowCalendar.get(Calendar.MINUTE)

        val targetCalendar = Calendar.getInstance()
        targetCalendar.timeInMillis = currentSelectedDate

        targetCalendar.set(Calendar.HOUR_OF_DAY, currentHour)
        targetCalendar.set(Calendar.MINUTE, currentMinute)

        var selectedTimestamp = targetCalendar.timeInMillis
        val builder = MaterialAlertDialogBuilder(this)
        val view = layoutInflater.inflate(R.layout.dialog_add_event, null)

        val etTitle = view.findViewById<EditText>(R.id.et_event_title)
        val etDesc = view.findViewById<EditText>(R.id.et_event_desc)
        val cbAllDay = view.findViewById<CheckBox>(R.id.cb_all_day)
        val btnPickTime = view.findViewById<Button>(R.id.btn_pick_time)

        val defaultColor = ContextCompat.getColor(this, R.color.accent_orange)
        val timeFormat = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())

        btnPickTime.text = timeFormat.format(java.util.Date(selectedTimestamp))

        cbAllDay.setOnCheckedChangeListener { _, isChecked ->
            btnPickTime.isEnabled = !isChecked
            if (isChecked) {
                btnPickTime.alpha = 0.5f
                btnPickTime.text = "Toute la journée"
            } else {
                btnPickTime.alpha = 1.0f
                btnPickTime.text = timeFormat.format(java.util.Date(selectedTimestamp))
            }
        }

        btnPickTime.setOnClickListener {
            val timeCal = Calendar.getInstance()
            timeCal.timeInMillis = selectedTimestamp

            TimePickerDialog(this, { _, hour, minute ->
                timeCal.set(Calendar.HOUR_OF_DAY, hour)
                timeCal.set(Calendar.MINUTE, minute)

                selectedTimestamp = timeCal.timeInMillis
                btnPickTime.text = String.format("%02d:%02d", hour, minute)
            }, timeCal.get(Calendar.HOUR_OF_DAY), timeCal.get(Calendar.MINUTE), true).show()
        }

        val dialog = builder.setView(view)
            .setTitle("Nouvel événement")
            .setPositiveButton("Ajouter", null)
            .setNegativeButton("Annuler", null)
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val title = etTitle.text.toString().trim()
            val desc = etDesc.text.toString().trim()

            if (title.isEmpty()) {
                etTitle.error = "Le titre est obligatoire"
            } else {
                val event = Event(
                    title = title,
                    description = desc.ifEmpty { null },
                    timestamp = selectedTimestamp, // Le timestamp est maintenant correct
                    color = defaultColor,
                    isAllDay = cbAllDay.isChecked
                )
                viewModel.addEvent(event)
                dialog.dismiss()
            }
        }
    }

    private fun showDeleteConfirmationDialog(event: Event) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Supprimer l'événement ?")
            .setMessage("Voulez-vous vraiment supprimer \"${event.title}\" ?")
            .setNegativeButton("Annuler", null)
            .setPositiveButton("Supprimer") { _, _ ->
                viewModel.deleteEvent(event.id)
            }
            .show()
    }
}