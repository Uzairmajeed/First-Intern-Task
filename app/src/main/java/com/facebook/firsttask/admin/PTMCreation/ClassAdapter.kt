package com.facebook.firsttask.admin.PTMCreation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.facebook.firsttask.R

class ClassAdapter(private val classList: List<String>) : RecyclerView.Adapter<ClassAdapter.ClassViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_class, parent, false)
        return ClassViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        val className = classList[position]
        holder.classNameTextView.text = className
    }

    override fun getItemCount(): Int {
        return classList.size
    }

    class ClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val classNameTextView: TextView = itemView.findViewById(R.id.classNameCheckbox)
    }
}
