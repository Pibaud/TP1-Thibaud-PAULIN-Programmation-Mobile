package com.example.trainapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trainapp.R
import com.example.trainapp.model.Journey

class JourneyAdapter(
    private val onItemClick: (Journey) -> Unit
) : RecyclerView.Adapter<JourneyAdapter.JourneyViewHolder>() {

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

        val firstTransportSection = journey.sections.firstOrNull {
            it.type == "public_transport" || it.type == "street_network"
        } ?: journey.sections.first() // Fallback au cas où

        val lastTransportSection = journey.sections.lastOrNull {
            it.type == "public_transport" || it.type == "street_network"
        } ?: journey.sections.last()

        val regex = Regex("\\s\\(.*\\)")
        val departureStation = firstTransportSection.from?.name?.replace(regex, "")?.trim()
        val arrivalStation = lastTransportSection.to?.name?.replace(regex, "")?.trim()

        holder.tvDepartureStation.text = departureStation
        holder.tvArrivalStation.text = arrivalStation

        if (journey.nbTransfers > 0) {
            val transferText = "${journey.nbTransfers} correspondance${if (journey.nbTransfers > 1) "s" else ""}"
            holder.tvTrainInfo.text = transferText
            holder.tvTrainInfo.setTextColor(Color.parseColor("#FCA311"))
        } else {
            val info = firstTransportSection.displayInformations
            val trainMode = info?.physicalMode ?: info?.commercialMode ?: "Train"
            val trainNum = info?.headsign ?: ""

            holder.tvTrainInfo.text = if (trainNum.isNotEmpty()) "$trainMode $trainNum" else trainMode
            holder.tvTrainInfo.setTextColor(Color.parseColor("#14213D"))
        }

        holder.tvDeparture.text = formatTime(journey.departureDateTime)
        holder.tvArrival.text = formatTime(journey.arrivalDateTime)

        val totalMinutes = journey.duration / 60
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        holder.tvDuration.text = if (hours > 0) "${hours}h${minutes.toString().padStart(2, '0')}" else "${minutes} min"

        holder.itemView.setOnClickListener {
            onItemClick(journey)
        }
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