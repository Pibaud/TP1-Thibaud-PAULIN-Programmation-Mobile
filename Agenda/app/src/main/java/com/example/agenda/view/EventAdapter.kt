package com.example.agenda.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.agenda.R
import com.example.agenda.model.Event
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventAdapter(private val onDeleteClick: (Event) -> Unit) :
    ListAdapter<Event, EventAdapter.EventViewHolder>(EventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event, onDeleteClick, position, itemCount)
    }

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvTitle: TextView = view.findViewById(R.id.tv_event_title)
        private val tvDesc: TextView = view.findViewById(R.id.tv_event_desc)
        private val tvTime: TextView = view.findViewById(R.id.tv_event_time)

        // Les lignes pointillées au final ne marchent pas
        private val lineTop: View = view.findViewById(R.id.line_top)
        private val lineBottom: View = view.findViewById(R.id.line_bottom)

        fun bind(event: Event, onDeleteClick: (Event) -> Unit, position: Int, totalItems: Int) {
            tvTitle.text = event.title

            if (event.description.isNullOrEmpty()) {
                tvDesc.visibility = View.GONE
            } else {
                tvDesc.text = event.description
                tvDesc.visibility = View.VISIBLE
            }

            if (event.isAllDay) {
                tvTime.text = "Journée"
                tvTime.textSize = 12f
            } else {
                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                tvTime.text = sdf.format(Date(event.timestamp))
                tvTime.textSize = 14f
            }

            lineTop.visibility = if (position == 0) View.INVISIBLE else View.VISIBLE

            lineBottom.visibility = if (position == totalItems - 1) View.INVISIBLE else View.VISIBLE

            itemView.setOnLongClickListener {
                onDeleteClick(event)
                true
            }
        }
    }

    class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Event, newItem: Event) = oldItem == newItem
    }
}