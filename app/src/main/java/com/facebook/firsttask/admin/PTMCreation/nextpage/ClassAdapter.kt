package com.facebook.firsttask.admin.PTMCreation.nextpage

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.facebook.firsttask.R

class ClassAdapter(
    private val classList: List<String>,
    private val teacherList: List<String>,
    private val timeList: List<String>
) : RecyclerView.Adapter<ClassAdapter.ClassViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_class, parent, false)
        return ClassViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        Log.d("AdapterDebug", "Position: $position, classList size: ${classList.size}, teacherList size: ${teacherList.size}")

        if (position < classList.size && position < teacherList.size) {
            val className = classList[position]
            val teacherName = teacherList[position]
            holder.classNameTextView.text = className
            holder.teacherNameTextView.text = teacherName

            // Format time list
            val formattedTimeList = formatTimeList(timeList)

            // Set up time slots spinner
            val spinnerAdapter = ArrayAdapter(holder.itemView.context, android.R.layout.simple_spinner_item, formattedTimeList)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            holder.timeSlotsSpinner.adapter = spinnerAdapter

            // Set up ListPopupWindow
            val popup = ListPopupWindow(holder.itemView.context)
            popup.setAdapter(spinnerAdapter)
            popup.anchorView = holder.timeSlotsSpinner
            popup.height = calculatePopupHeight(holder.timeSlotsSpinner, 5) // Set height for 5 items
            popup.setOnItemClickListener { parent, view, position, id ->
                // Set the selected item in the spinner
                holder.timeSlotsSpinner.setSelection(position)
                popup.dismiss()
            }

            // Show popup on spinner click
            holder.timeSlotsSpinner.setOnTouchListener { _, _ ->
                popup.show()
                true
            }

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
    }
}
