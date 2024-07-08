package com.facebook.firsttask.admin.PTMCreation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import com.facebook.firsttask.R

class TimeSelectionAdapter(private val timeSelections: MutableList<TimeSelection>) :
    RecyclerView.Adapter<TimeSelectionAdapter.TimeSelectionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSelectionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_time_selection, parent, false)
        return TimeSelectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimeSelectionViewHolder, position: Int) {
        holder.bind(timeSelections[position])
         holder.itemView.findViewById<View>(R.id.deleteButton).setOnClickListener{
             // Remove item from list
             timeSelections.removeAt(position)
             // Notify adapter about item removal
             notifyDataSetChanged()
         }
    }

    override fun getItemCount(): Int = timeSelections.size

    class TimeSelectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val startTimeSpinner: Spinner = itemView.findViewById(R.id.startTimeSpinner)
        private val endTimeSpinner: Spinner = itemView.findViewById(R.id.endTimeSpinner)

        fun bind(timeSelection: TimeSelection) {
            // Example: Set up spinners for start and end times
            val startTimeSpinnerAdapter = ArrayAdapter<String>(
                itemView.context,
                android.R.layout.simple_spinner_item,
                arrayOf("8 AM", "9 AM", "10 AM", "11 00 AM") // Example data
            )
            startTimeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            startTimeSpinner.adapter = startTimeSpinnerAdapter

            val endTimeSpinnerAdapter = ArrayAdapter<String>(
                itemView.context,
                android.R.layout.simple_spinner_item,
                arrayOf("12 PM", "1 PM", "2 PM", "3 PM") // Example data
            )
            endTimeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            endTimeSpinner.adapter = endTimeSpinnerAdapter
        }
    }
}

