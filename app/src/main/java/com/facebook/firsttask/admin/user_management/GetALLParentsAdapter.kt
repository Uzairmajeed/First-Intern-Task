package com.facebook.firsttask.admin.user_management

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.facebook.firsttask.databinding.ItemGetallparentsBinding

class GetALLParentsAdapter(private val parents: List<ParentData>) : RecyclerView.Adapter<GetALLParentsAdapter.ParentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val binding = ItemGetallparentsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ParentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ParentViewHolder, position: Int) {
        val parent = parents[position]
        with(holder.binding) {
            idView.text = parent.id.toString()
            parentView.text = "${parent.firstName}${parent.lastName}\n${parent.firstName2}${parent.lastName2}"
            emailView.text =  "${parent.email}\n${parent.email2}"
            childrenView.text = parent.childrens.size.toString()
        }
    }

    override fun getItemCount(): Int = parents.size

    class ParentViewHolder(val binding: ItemGetallparentsBinding) : RecyclerView.ViewHolder(binding.root)
}
