package com.facebook.firsttask.admin.PTMCreation

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.facebook.firsttask.R

class ClassAdapter(private val classList: List<String>, private val teacherList: List<String>) : RecyclerView.Adapter<ClassAdapter.ClassViewHolder>() {

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
        } else {
            Log.e("AdapterDebug", "Invalid position: $position")
        }
    }


    override fun getItemCount(): Int {
        // Return the minimum size of the two lists
        return minOf(classList.size, teacherList.size)
    }

    class ClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val classNameTextView: TextView = itemView.findViewById(R.id.classNameCheckbox)
        val teacherNameTextView:TextView = itemView.findViewById(R.id.teacherCheckBox)
    }
}
