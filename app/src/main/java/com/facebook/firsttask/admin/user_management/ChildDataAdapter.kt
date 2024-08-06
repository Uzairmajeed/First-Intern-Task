package com.facebook.firsttask.admin.user_management

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.facebook.firsttask.PreferencesManager
import com.facebook.firsttask.databinding.ItemAllchildDataUsermanageBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
class ChildDataAdapter(
    private val childDataList: List<ChildData>,
    private val parentName: String?,
    private val parentId: Int?,
    private val fragmentManager: FragmentManager,
    private val context: Context,
    private val childDataDialoglistner: OnMakeChanges
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

            // Store the original state
            val originalState = childData.isActive

            binding.statusSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (originalState != isChecked) {
                    showConfirmationDialog(childData.childId, isChecked, originalState)
                }
            }

            binding.editButton.setOnClickListener {
                val editChildDialog = childData.firstName?.let { it1 ->
                    childData.lastName?.let { it2 ->
                        EditChildDialogFragment.newInstance(
                            it1,
                            it2,
                            parentName ?: "",
                            parentId ?: 0,
                            childDataDialoglistner

                        )
                    }
                }
                if (editChildDialog != null) {
                    editChildDialog.show(fragmentManager, "EditChildDialogFragment")
                }
            }

        }
    }

    private fun showConfirmationDialog(childId: Int, newState: Boolean, originalState: Boolean) {
        AlertDialog.Builder(context)
            .setTitle("Change Status")
            .setMessage("Are you sure you want to change the status?")
            .setPositiveButton("Yes") { _, _ ->
                updateStatus(childId, newState, originalState)
            }
            .setNegativeButton("Cancel") { _, _ ->
                // Revert switch state if canceled
                (context as? FragmentActivity)?.let {
                    it.lifecycleScope.launch {
                        notifyDataSetChanged() // Refresh the list if needed
                    }
                }
            }
            .show()
    }

    private fun updateStatus(childId: Int, newState: Boolean, originalState: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            val preferencesManager = PreferencesManager(context)
            val authToken = preferencesManager.getAuthToken()
            val networkForUserManagement = authToken?.let { NetworkForUserManagement(it, context) }
            val success = networkForUserManagement?.changeStatus(childId, newState) ?: false

            withContext(Dispatchers.Main) {
                if (success) { // Update the status in the UI
                    notifyDataSetChanged() // Refresh the list if needed
                    childDataDialoglistner.onChange()
                } else {
                    // Show a toast message if the status cannot be changed
                    Toast.makeText(context, "Cannot change status", Toast.LENGTH_SHORT).show()
                    // Optionally, you might want to revert the switch state if needed
                }
            }
        }
    }
}
