package com.example.trainapp.ui.adapter

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import com.example.trainapp.data.api.Place

class NoFilterAdapter(context: Context, layout: Int) :
    ArrayAdapter<String>(context, layout, ArrayList()) {

    private var fullPlaces = listOf<Place>()
    private var displayNames = ArrayList<String>()

    fun updateData(newPlaces: List<Place>, names: List<String>) {
        this.fullPlaces = newPlaces
        this.displayNames.clear()
        this.displayNames.addAll(names)

        clear()
        addAll(names)
        notifyDataSetChanged()
    }

    fun getPlaceAt(position: Int): Place = fullPlaces[position]

    override fun getCount(): Int = displayNames.size
    override fun getItem(position: Int): String? = displayNames[position]

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                results.values = displayNames
                results.count = displayNames.size
                return results
            }
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                notifyDataSetChanged()
            }
        }
    }
}