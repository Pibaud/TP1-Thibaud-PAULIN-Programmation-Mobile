package com.example.trainapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trainapp.R
import com.example.trainapp.data.api.Journey

class JourneyAdapter : RecyclerView.Adapter<JourneyAdapter.JourneyViewHolder>() {

    private var journeys = listOf<Journey>()

    fun setJourneys(newJourneys: List<Journey>) {
        this.journeys = newJourneys
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JourneyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_journey, parent, false)
        return JourneyViewHolder(view)
    }

    override fun onBindViewHolder(holder: JourneyViewHolder, position: Int) {
        val journey = journeys[position]

        // On cherche la section transport public
        val trainSection = journey.sections.find { it.type == "public_transport" }
        val info = trainSection?.displayInformations

        // Affichage des gares de départ et d'arrivée de la section train
        // On applique le même nettoyage Regex que pour la recherche si on veut enlever les parenthèses
        val departureStation = trainSection?.from?.name?.replace(Regex("\\s\\(.*\\)"), "")?.trim() ?: "Départ"
        val arrivalStation = trainSection?.to?.name?.replace(Regex("\\s\\(.*\\)"), "")?.trim() ?: "Arrivée"

        holder.tvDepartureStation.text = departureStation
        holder.tvArrivalStation.text = arrivalStation

        // Reste du code (Horaires et Durée)
        holder.tvTrainInfo.text = "${info?.commercialMode ?: "Train"} n°${info?.headsign ?: ""}"
        holder.tvDeparture.text = formatTime(journey.departureDateTime)
        holder.tvArrival.text = formatTime(journey.arrivalDateTime)

        val totalMinutes = journey.duration / 60
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        holder.tvDuration.text = if (hours > 0) "${hours}h${minutes.toString().padStart(2, '0')}" else "${minutes} min"
    }

    override fun getItemCount() = journeys.size

    private fun formatTime(dateStr: String): String {
        return try {
            "${dateStr.substring(9, 11)}:${dateStr.substring(11, 13)}"
        } catch (e: Exception) { "--:--" }
    }

    class JourneyViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvDeparture: TextView = v.findViewById(R.id.tv_departure_time)
        val tvArrival: TextView = v.findViewById(R.id.tv_arrival_time)
        val tvDuration: TextView = v.findViewById(R.id.tv_duration)
        val tvTrainInfo: TextView = v.findViewById(R.id.tv_train_info)
        val tvDepartureStation: TextView = v.findViewById(R.id.tv_departure_station)
        val tvArrivalStation: TextView = v.findViewById(R.id.tv_arrival_station)
    }
}