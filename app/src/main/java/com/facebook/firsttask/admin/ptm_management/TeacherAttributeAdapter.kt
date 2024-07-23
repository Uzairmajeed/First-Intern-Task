package com.facebook.firsttask.admin.ptm_management

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.firsttask.R

class TeacherAttributeAdapter(
    private val teacherAttributes: List<TeacherAttribute>,
    private val fragmentManager: FragmentManager,

    ) : RecyclerView.Adapter<TeacherAttributeAdapter.TeacherAttributeViewHolder>() {

    class TeacherAttributeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val teacherName: TextView = itemView.findViewById(R.id.teacherName)
        val teacherEmail: TextView = itemView.findViewById(R.id.teacherEmail)
        val locationName: TextView = itemView.findViewById(R.id.locationName)
        val className: TextView = itemView.findViewById(R.id.className)
        val editLocationButton: ImageButton = itemView.findViewById(R.id.editlocationButton) // Add this line

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherAttributeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_teacher_attribute, parent, false)
        return TeacherAttributeViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeacherAttributeViewHolder, position: Int) {
        val teacherAttribute = teacherAttributes[position]
        holder.teacherName.text = teacherAttribute.teacherName
        holder.teacherEmail.text = teacherAttribute.teacherEmail
        holder.locationName.text = teacherAttribute.locationName ?: "N/A"
        holder.className.text = teacherAttribute.className

        Log.d("Location ID,Teacher ID ,PTmid", "${teacherAttribute.locationId.toString()}" +
                "${teacherAttribute.teacherAttId.toString()}")

        holder.editLocationButton.setOnClickListener {
            val dialogFragment = teacherAttribute.teacherName?.let { it1 ->
                EditLocationDialogFragment.newInstance(
                    it1
                )
            }
            if (dialogFragment != null) {
                dialogFragment.show(fragmentManager, "EditLocationDialog")
            }
        }
    }

    override fun getItemCount(): Int {
        return teacherAttributes.size
    }
}
