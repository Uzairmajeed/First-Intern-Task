package com.facebook.firsttask.admin.class_management

import android.view.LayoutInflater
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val binding = ItemGetclassesformanagementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClassViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
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

    override fun getItemCount() = classList.size
}
