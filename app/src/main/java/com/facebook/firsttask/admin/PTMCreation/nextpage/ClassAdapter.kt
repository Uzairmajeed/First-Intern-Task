package com.facebook.firsttask.admin.PTMCreation.nextpage

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.facebook.firsttask.R

class ClassAdapter(
    private val classList: List<String>,
    private val teacherList: List<String>,
    private val timeList: List<String>,
    private val locationList: List<String>
) : RecyclerView.Adapter<ClassAdapter.ClassViewHolder>() {

    private val selectedTimesList = mutableListOf<String>()
    private var spinnerAdapter: CustomArrayAdapter? = null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_class, parent, false)
        return ClassViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        if (position < classList.size && position < teacherList.size) {
            val className = classList[position]
            val teacherName = teacherList[position]
            holder.classNameTextView.text = className
            holder.teacherNameTextView.text = teacherName

            // Format time list
            val formattedTimeList = formatTimeList(timeList)

            // Initialize or update the adapter if already initialized
            if (spinnerAdapter == null) {
                spinnerAdapter = CustomArrayAdapter(
                    holder.itemView.context,
                    android.R.layout.simple_spinner_item,
                    formattedTimeList
                )
                spinnerAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                holder.timeSlotsSpinner.adapter = spinnerAdapter
            } else {
                spinnerAdapter?.clear()
                spinnerAdapter?.addAll(formattedTimeList)
                spinnerAdapter?.notifyDataSetChanged()
            }

            // Set up ListPopupWindow
            val popup = ListPopupWindow(holder.itemView.context)
            popup.setAdapter(spinnerAdapter)
            popup.anchorView = holder.timeSlotsSpinner
            popup.height = calculatePopupHeight(holder.timeSlotsSpinner, 5) // Set height for 5 items
            popup.setOnItemClickListener { parent, view, position, id ->
                // Add selected time to list
                val selectedTime = formattedTimeList[position]
                // Update spinner adapter with selected items
                holder.timeSlotsSpinner.setSelection(position)
                selectedTimesList.add(selectedTime)

                // Log selectedTimesList
                Log.d("AdapterDebug", "Selected Times List: $selectedTimesList")

                // Notify adapter of data change
                spinnerAdapter?.notifyDataSetChanged()

                popup.dismiss()
            }

            // Show popup on spinner click
            holder.timeSlotsSpinner.setOnTouchListener { _, _ ->
                popup.show()
                true
            }

            // Set up location spinner
            val locationAdapter = ArrayAdapter(holder.itemView.context, android.R.layout.simple_spinner_item, locationList)
            locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            holder.locationSlotsSpinner.adapter = locationAdapter

        } else {
            Log.e("AdapterDebug", "Invalid position: $position")
        }
    }

    // Format time list to "1:00 AM - 1:10 AM" format
    private fun formatTimeList(timeList: List<String>): List<String> {
        val formattedTimeList = mutableListOf<String>()
        for (i in 0 until timeList.size - 1) {
            formattedTimeList.add("${timeList[i]} - ${timeList[i + 1]}")
        }
        return formattedTimeList
    }

    // Calculate popup window height based on item count
    private fun calculatePopupHeight(spinner: Spinner, itemCount: Int): Int {
        val itemHeight = spinner.resources.getDimensionPixelSize(R.dimen.picker_row_height)
        val dropdownMaxHeight = spinner.resources.getDimensionPixelSize(R.dimen.dropdown_max_height)
        val totalHeight = itemHeight * itemCount
        return if (totalHeight < dropdownMaxHeight) totalHeight else dropdownMaxHeight
    }

    override fun getItemCount(): Int {
        // Return the minimum size of the two lists
        return minOf(classList.size, teacherList.size)
    }

    class ClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val classNameTextView: TextView = itemView.findViewById(R.id.classNameCheckbox)
        val teacherNameTextView: TextView = itemView.findViewById(R.id.teacherCheckBox)
        val timeSlotsSpinner: Spinner = itemView.findViewById(R.id.timeSlotsSpinner)
        val locationSlotsSpinner: Spinner = itemView.findViewById(R.id.locationSpinner)
    }


    // Custom ArrayAdapter to handle dropdown view customization
    inner class CustomArrayAdapter(
        context: Context,
        resource: Int,
        objects: List<String>
    ) : ArrayAdapter<String>(context, resource, objects) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return customizeView(position, super.getView(position, convertView, parent))
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            return customizeView(position, super.getDropDownView(position, convertView, parent))
        }

        private fun customizeView(position: Int, view: View): View {
            val textView = view as TextView
            val item = getItem(position)

            // Check if item is in selectedTimesList
            if (selectedTimesList.contains(item)) {
                // Apply green background for selected items
                textView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
            } else {
                // Reset background color for unselected items
                textView.setBackgroundColor(Color.TRANSPARENT) // or set to default color
            }

            return textView
        }
    }
}