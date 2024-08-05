package com.facebook.firsttask.admin.user_management

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.firsttask.databinding.ItemAllchildDataUsermanageBinding

class ChildDataAdapter(
    private val childDataList: List<ChildData>,
    private val parentName: String?,
    private val childFragmentManager: FragmentManager
) : RecyclerView.Adapter<ChildDataAdapter.ChildDataViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildDataViewHolder {
        val binding = ItemAllchildDataUsermanageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChildDataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChildDataViewHolder, position: Int) {
        val childData = childDataList[position]
        holder.bind(childData)
    }

    override fun getItemCount(): Int = childDataList.size

    inner class ChildDataViewHolder(private val binding: ItemAllchildDataUsermanageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(childData: ChildData) {
            binding.idView.text = childData.childId.toString()
            binding.childView.text = "${childData.firstName}${childData.lastName}"
            binding.classView.text = childData.className
            binding.statusSwitch.isChecked = childData.isActive
            // Set text visibility based on switch state
            binding.statusTextView.text = if (childData.isActive) "Active" else "Inactive"
            // Optionally, you can set up the ImageButton here if needed
            // e.g., binding.editButton.setOnClickListener { /* Handle edit action */ }
        }
    }
}
