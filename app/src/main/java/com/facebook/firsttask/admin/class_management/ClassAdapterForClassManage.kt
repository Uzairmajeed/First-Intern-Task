package com.facebook.firsttask.admin.class_management

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.firsttask.databinding.ItemGetclassesformanagementBinding

class ClassAdapterForClassManage(
    private val classList: List<ClassData>,
    private val fragmentManager: FragmentManager,
    private val listner: OnUpdatedCLassNameListner
) :
    RecyclerView.Adapter<ClassAdapterForClassManage.ClassViewHolder>() {

    class ClassViewHolder(val binding: ItemGetclassesformanagementBinding) : RecyclerView.ViewHolder(binding.root) {
        val classNameTextView = binding.textViewClassName
        val editButton = binding.imageButtonEditClassName
        val noDataTextView = binding.noDataTextView // Add this line to reference the "No data available" TextView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val binding = ItemGetclassesformanagementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClassViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        // Check if the list is empty
        if (classList.isEmpty()) {
            // Show the "No data available" message
            holder.noDataTextView.visibility = View.VISIBLE
            holder.classNameTextView.visibility =View.GONE
            holder.editButton.visibility =View.GONE

        } else {
            // Hide the "No data available" message and bind data
            holder.noDataTextView.visibility = View.GONE
            val classData = classList[position]
            holder.classNameTextView.text = "${classData.className} - ${classData.sectionName} "

            holder.editButton.setOnClickListener {
                val dialogFragment = EditClassDialogFragment.newInstance(
                    classData.className,
                    classData.sectionName,
                    classData.classId,
                    listner
                )
                dialogFragment.show(fragmentManager, "EditClassDialog")
            }
        }
    }

    override fun getItemCount() = if (classList.isEmpty()) 1 else classList.size // Show 1 item if no data
}
