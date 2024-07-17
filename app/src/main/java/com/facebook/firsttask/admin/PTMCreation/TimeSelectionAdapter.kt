package com.facebook.firsttask.admin.PTMCreation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
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

    fun getTimeSelectionData(): List<TimeSelection> {
        return timeSelections
    }

    class TimeSelectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val startTimeSpinner: Spinner = itemView.findViewById(R.id.startTimeSpinner)
        private val endTimeSpinner: Spinner = itemView.findViewById(R.id.endTimeSpinner)

        fun bind(timeSelection: TimeSelection) {
            val startTimes = arrayOf("01:00 AM", "02:00 AM", "03:00 AM", "04:00 AM", "05:00 AM",
                "06:00 AM", "07:00 AM", "08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM",
                "12:00 PM", "01:00 PM", "02:00 PM", "03:00 PM", "04:00 PM", "05:00 PM",
                "06:00 PM", "07:00 PM", "08:00 PM", "09:00 PM", "10:00 PM", "11:00 PM",
                "12:00 AM")
            val endTimes = arrayOf("01:00 AM", "02:00 AM", "03:00 AM", "04:00 AM", "05:00 AM",
                "06:00 AM", "07:00 AM", "08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM",
                "12:00 PM", "01:00 PM", "02:00 PM", "03:00 PM", "04:00 PM", "05:00 PM",
                "06:00 PM", "07:00 PM", "08:00 PM", "09:00 PM", "10:00 PM", "11:00 PM",
                "12:00 AM")

            setupSpinner(startTimeSpinner, startTimes, timeSelection.startTime) {
                timeSelection.startTime = it
            }
            setupSpinner(endTimeSpinner, endTimes, timeSelection.endTime) {
                timeSelection.endTime = it
            }
        }

        private fun setupSpinner(
            spinner: Spinner,
            data: Array<String>,
            selectedValue: String,
            onItemSelected: (String) -> Unit
        ) {
            val adapter = ArrayAdapter(spinner.context, android.R.layout.simple_spinner_item, data)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedItem = data[position]
                    onItemSelected(selectedItem)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            val position = adapter.getPosition(selectedValue)
            spinner.setSelection(position)
        }
    }
}




