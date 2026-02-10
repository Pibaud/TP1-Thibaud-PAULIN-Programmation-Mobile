package com.example.trainapp.ui.view

import android.os.Bundle
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import android.content.Context
import com.example.trainapp.R
import androidx.core.text.HtmlCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.view.Gravity
import com.example.trainapp.data.repository.TrainRepository
import android.widget.Button
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trainapp.ui.adapter.JourneyAdapter
import com.example.trainapp.ui.adapter.NoFilterAdapter
import com.example.trainapp.di.RetrofitClient
import com.example.trainapp.ui.viewmodel.TrainViewModel
import kotlinx.coroutines.launch
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.example.trainapp.data.api.Journey
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
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

        val titleView = findViewById<TextView>(R.id.tv_welcome_title)
        val rawHtml = getString(R.string.search_welcome_message)
        titleView.text = HtmlCompat.fromHtml(rawHtml, HtmlCompat.FROM_HTML_MODE_LEGACY) // pour mettre la fin du titre en couleur

        val rvJourneys = findViewById<RecyclerView>(R.id.rv_journeys)

        journeyAdapter = JourneyAdapter { selectedJourney ->
            showJourneyDetails(selectedJourney)
        }

        rvJourneys.layoutManager = LinearLayoutManager(this)
        rvJourneys.adapter = journeyAdapter

        findViewById<Button>(R.id.btn_next_time).setOnClickListener {
            viewModel.loadNext { newTimestamp ->
                showDateChangeAlert(newTimestamp)
            }
        }

        findViewById<Button>(R.id.btn_prev_time).setOnClickListener {
            viewModel.shiftTime(-3) { newTimestamp ->
                showDateChangeAlert(newTimestamp) // pas de prev ici car l'api indique les trains qui ARRIVENT avant et pas ceyx qui PARTENT avant
            }
        }

        findViewById<Button>(R.id.btn_select_time).setOnClickListener {
            showTimePicker()
        }
// Initialiser le texte du bouton avec la valeur par défaut du ViewModel (06:00)
        findViewById<Button>(R.id.btn_select_time).text = String.format("%02d:%02d", viewModel.selectedHour, viewModel.selectedMinute)

        val btnDate = findViewById<Button>(R.id.btn_select_date)
        btnDate.setOnClickListener {
            showDatePicker()
        }

        val autoFrom = findViewById<AutoCompleteTextView>(R.id.auto_complete_from)
        val autoTo = findViewById<AutoCompleteTextView>(R.id.auto_complete_to)

        setupAutoComplete(autoFrom)
        setupAutoComplete(autoTo)

        findViewById<View>(R.id.btn_swap).setOnClickListener {
            val autoFrom = findViewById<AutoCompleteTextView>(R.id.auto_complete_from)
            val autoTo = findViewById<AutoCompleteTextView>(R.id.auto_complete_to)

            val tempText = autoFrom.text.toString()
            val tempId = fromId

            autoFrom.setText(autoTo.text.toString(), false)
            fromId = toId

            autoTo.setText(tempText, false)
            toId = tempId

            autoFrom.clearFocus()
            autoTo.clearFocus()
            hideKeyboard(it)

            if (fromId != null && toId != null) {
                viewModel.clearHistory()
                searchJourneys()
            }
        }

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

        changeButtonVisibility()

        if (fromId != null && toId != null) {
            if (fromId != viewModel.lastFromId || toId != viewModel.lastToId) { // permet de changer de gare sans garder les anciens résultats
                viewModel.clearHistory()
            }

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
        val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        findViewById<Button>(R.id.btn_select_date).text = displayFormat.format(Date(selection))

        val date = Date(selection)

        findViewById<Button>(R.id.btn_select_date).text = displayFormat.format(date)

        if (viewModel.selectedTimestamp != selection) {
            viewModel.clearHistory()
        }

        viewModel.updateDateTimeFromComponents(selection, viewModel.selectedHour, viewModel.selectedMinute)

        searchJourneys()
    }

    datePicker.show(supportFragmentManager, "DATE_PICKER")
}

    private fun showTimePicker() {
        // On prend l'heure actuelle du ViewModel ou 6h par défaut
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(viewModel.selectedHour)
            .setMinute(viewModel.selectedMinute)
            .setTitleText("Heure de départ")
            .build()

        picker.addOnPositiveButtonClickListener {
            val hour = picker.hour
            val minute = picker.minute

            val formattedTime = String.format("%02d:%02d", hour, minute)
            findViewById<Button>(R.id.btn_select_time).text = formattedTime

            viewModel.setTime(hour, minute)

            if (viewModel.selectedTimestamp == null) {
                val today = MaterialDatePicker.todayInUtcMilliseconds()
                viewModel.updateDateTimeFromComponents(today, hour, minute)
                val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                findViewById<Button>(R.id.btn_select_date).text = df.format(Date(today))
            }
        }
        picker.show(supportFragmentManager, "TIME_PICKER")
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

                viewModel.clearHistory()

                val btnDate = findViewById<Button>(R.id.btn_select_date)
                btnDate.text = displayFormat.format(Date(newTimestamp))

                searchJourneys()
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun changeButtonVisibility() {
        val btnPrev = findViewById<View>(R.id.btn_prev_time)
        val btnNext = findViewById<View>(R.id.btn_next_time)

        val visibility = if (fromId != null && toId != null) View.VISIBLE else View.GONE

        btnPrev.visibility = visibility
        btnNext.visibility = visibility
    }

    private fun showJourneyDetails(journey: Journey) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_journey_details, null)
        val container = view.findViewById<LinearLayout>(R.id.details_container)

        // On parcourt toutes les sections pour construire l'affichage dynamique
        for (section in journey.sections) {
            // On ne s'intéresse qu'aux sections de transport public (Train, Bus) ou d'attente (Correspondance)
            if (section.type == "public_transport") {
                // Création dynamique d'une vue pour le train
                val trainView = LayoutInflater.from(this).inflate(R.layout.item_section_train, container, false)

                val tvDepTime = trainView.findViewById<TextView>(R.id.tv_section_dep_time)
                val tvDepStation = trainView.findViewById<TextView>(R.id.tv_section_dep_station)
                val tvArrTime = trainView.findViewById<TextView>(R.id.tv_section_arr_time)
                val tvArrStation = trainView.findViewById<TextView>(R.id.tv_section_arr_station)
                val tvInfo = trainView.findViewById<TextView>(R.id.tv_section_info)

                // Formatage des heures (ex: 20260211T075300 -> 07:53)
                tvDepTime.text = formatTime(section.departureDateTime)
                tvArrTime.text = formatTime(section.arrivalDateTime)

                tvDepStation.text = section.from?.name
                tvArrStation.text = section.to?.name

                // Ex: TER - K13 [cite: 92, 93]
                val mode = section.displayInformations?.physicalMode ?: "Train"
                val number = section.displayInformations?.headsign ?: ""
                tvInfo.text = "$mode $number"

                container.addView(trainView)
            }
            else if (section.type == "waiting" || section.type == "transfer") {
                // Création dynamique d'une vue pour la correspondance
                val transferView = TextView(this)
                val durationMin = section.duration / 60
                transferView.text = "⏱ Correspondance : ${durationMin} min"
                transferView.setPadding(40, 20, 40, 20)
                transferView.setTextColor(getColor(R.color.accent_orange))
                transferView.gravity = Gravity.CENTER_VERTICAL
                container.addView(transferView)
            }
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    private fun formatTime(isoDate: String): String {
        if (isoDate.length < 13) return "..."
        return "${isoDate.substring(9, 11)}:${isoDate.substring(11, 13)}"
    }
}