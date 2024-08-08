package com.facebook.firsttask.admin.user_management

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.facebook.firsttask.R

class CustomSpinnerAdapter(
    context: Context,
    private val items: List<String>,
    private val selectedItems: MutableSet<String> // Maintain selected items
) : ArrayAdapter<String>(context, R.layout.spinner_item, items) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(R.layout.spinner_item, parent, false)
        val textView = view.findViewById<TextView>(R.id.spinner_item_text)

        val item = getItem(position) ?: ""
        textView.text = item

        // Update TextView color based on selection
        if (selectedItems.contains(item)) {
            textView.setTextColor(context.getColor(R.color.colorPrimary)) // Highlight selected items
        } else {
            textView.setTextColor(context.getColor(android.R.color.black)) // Default color
        }

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }

    fun toggleSelection(item: String) {
        if (selectedItems.contains(item)) {
            selectedItems.remove(item)
        } else {
            selectedItems.add(item)
        }
        notifyDataSetChanged()
    }

    fun getSelectedItems(): List<String> {
        return selectedItems.toList()
    }
}
