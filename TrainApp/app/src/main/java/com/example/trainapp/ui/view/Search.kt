package com.example.trainapp.ui.view

import android.os.Bundle
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import android.content.Context
import com.example.trainapp.R
import com.example.trainapp.data.repository.TrainRepository
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trainapp.ui.adapter.JourneyAdapter
import com.example.trainapp.ui.adapter.NoFilterAdapter
import com.example.trainapp.di.RetrofitClient
import com.example.trainapp.ui.viewmodel.TrainViewModel
import kotlinx.coroutines.launch
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class Search : AppCompatActivity() { // activity ne suffisait pas pour utiliser les co routines lifecyclescope
    private val repository = TrainRepository(RetrofitClient.instance)
    private val viewModel = TrainViewModel(repository)

    private lateinit var journeyAdapter: JourneyAdapter

    private var fromId: String? = null
    private var toId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        findViewById<Button>(R.id.btn_next_time).setOnClickListener {
            viewModel.shiftTime(3) { newTimestamp ->
                showDateChangeAlert(newTimestamp)
            }
        }

        findViewById<Button>(R.id.btn_prev_time).setOnClickListener {
            viewModel.shiftTime(-3) { newTimestamp ->
                showDateChangeAlert(newTimestamp)
            }
        }

        val btnDate = findViewById<Button>(R.id.btn_select_date)
        btnDate.setOnClickListener {
            showDatePicker()
        }

        val rvJourneys = findViewById<RecyclerView>(R.id.rv_journeys)
        journeyAdapter = JourneyAdapter()
        rvJourneys.layoutManager = LinearLayoutManager(this)
        rvJourneys.adapter = journeyAdapter

        val autoFrom = findViewById<AutoCompleteTextView>(R.id.auto_complete_from)
        val autoTo = findViewById<AutoCompleteTextView>(R.id.auto_complete_to)

        setupAutoComplete(autoFrom)
        setupAutoComplete(autoTo)

        lifecycleScope.launch {
            viewModel.journeys.collect { results ->
                journeyAdapter.setJourneys(results)
            }
        }
    }

    private fun setupAutoComplete(view: AutoCompleteTextView) {
        val adapter = NoFilterAdapter(this, R.layout.item_suggestion)
        view.setAdapter(adapter)
        view.threshold = 2

        view.addTextChangedListener { text ->
            viewModel.onQueryChanged(text.toString())
        }

        lifecycleScope.launch {
            viewModel.suggestions.collect { places ->
                if (view.hasFocus()) {
                    val names = places.map { place ->
                        when (place.type) {
                            "administrative_region" -> place.name
                            "stop_area" -> place.name.replace(Regex("\\s\\(.*\\)"), "").trim()
                            else -> place.name
                        }
                    }
                    adapter.updateData(places, names)
                    if (names.isNotEmpty()) {
                        view.showDropDown()
                    }
                }
            }
        }

        view.setOnItemClickListener { _, _, position, _ ->
            val selectedName = adapter.getItem(position)
            val selectedPlace = adapter.getPlaceAt(position)

            view.setText(selectedName, false)

            if (view.id == R.id.auto_complete_from) {
                fromId = selectedPlace.id
            } else {
                toId = selectedPlace.id
            }

            adapter.updateData(emptyList(), emptyList())
            view.clearFocus()
            hideKeyboard(view)

            searchJourneys()
        }
    }

    private fun hideKeyboard(view: android.view.View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun searchJourneys() {
        val fromId = fromId
        val toId = toId

        if (fromId != null && toId != null) {
            viewModel.setFromTo(fromId, toId)
            viewModel.findJourneys(fromId, toId)
        }
    }

private fun showDatePicker() {
    val today = MaterialDatePicker.todayInUtcMilliseconds()

    val initialSelection = viewModel.selectedTimestamp ?: today

    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar.timeInMillis = today
    calendar.add(Calendar.DAY_OF_MONTH, 23)//-1/N/+23 d'après la doc : il est possible de récupérer les données du jour précédent et d’aller les requêter sur les 23 jours suivants.
    val twentyThreeDaysLater = calendar.timeInMillis

    val constraints = CalendarConstraints.Builder()
        .setStart(today)
        .setEnd(twentyThreeDaysLater)
        .setValidator(CompositeDateValidator.allOf(listOf(
            DateValidatorPointForward.from(today),
            DateValidatorPointBackward.before(twentyThreeDaysLater)
        )))
        .build()

    val datePicker = MaterialDatePicker.Builder.datePicker()
        .setTitleText("Date du voyage")
        // On définit la sélection initiale ici
        .setSelection(initialSelection)
        .setCalendarConstraints(constraints)
        .build()

    datePicker.addOnPositiveButtonClickListener { selection ->
        val sdf = SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.getDefault())
        val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        val date = Date(selection)
        val formattedDate = sdf.format(date)

        findViewById<Button>(R.id.btn_select_date).text = displayFormat.format(date)

        // On sauvegarde les deux formats dans le ViewModel
        viewModel.setDateTime(formattedDate, selection)

        searchJourneys()
    }

    datePicker.show(supportFragmentManager, "DATE_PICKER")
}

    private fun showDateChangeAlert(newTimestamp: Long) {
        val displayFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
        val dateStr = displayFormat.format(Date(newTimestamp))

        AlertDialog.Builder(this)
            .setTitle("Changement de jour")
            .setMessage("Ce décalage vous fait passer au $dateStr. Voulez-vous continuer ?")
            .setPositiveButton("Oui") { _, _ ->
                val sdf = SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.getDefault())
                val formatted = sdf.format(Date(newTimestamp))
                viewModel.setDateTime(formatted, newTimestamp)

                val btnDate = findViewById<Button>(R.id.btn_select_date)
                btnDate.text = displayFormat.format(Date(newTimestamp))

                searchJourneys()
            }
            .setNegativeButton("Annuler", null)
            .show()
    }
}