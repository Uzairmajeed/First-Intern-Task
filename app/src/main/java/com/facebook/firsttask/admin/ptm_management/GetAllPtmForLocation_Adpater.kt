package com.facebook.firsttask.admin.ptm_management


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.firsttask.R

class GetAllPtmForLocation_Adpater(
    private val ptmList: List<PtmData>,
    private val fragmentManager: FragmentManager,
    private val onLocationUpdatedListener: OnLocationUpdatedListener // Add this parameter


) : RecyclerView.Adapter<GetAllPtmForLocation_Adpater.PtmViewHolder>() {

    class PtmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemDate: TextView = itemView.findViewById(R.id.itemDate)
        val itemDuration: TextView = itemView.findViewById(R.id.itemDuration)
        val itemWingName: TextView = itemView.findViewById(R.id.itemWingName)
        val itemActionButton: Button = itemView.findViewById(R.id.itemActionButton)
        val itemDropdownButton: ImageButton = itemView.findViewById(R.id.itemDropdownButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PtmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_getallptmsforlocation, parent, false)
        return PtmViewHolder(view)
    }

    override fun onBindViewHolder(holder: PtmViewHolder, position: Int) {
        val ptmItem = ptmList[position]
        holder.itemDate.text = ptmItem.date
        holder.itemDuration.text = ptmItem.duration
        holder.itemWingName.text = ptmItem.wings.firstOrNull()?.wingName ?: "N/A"
        holder.itemActionButton.text = "+${ptmItem.wings.size}"


        // Set click listeners if necessary
        holder.itemActionButton.setOnClickListener {
            val wingNames = ptmItem.wings.map { it.wingName }
            val dialogFragment = WingListDialogFragment.newInstance(wingNames)
            dialogFragment.show(fragmentManager, "WingListDialogFragment")
        }

        holder.itemDropdownButton.setOnClickListener {
            val dialogFragment = TeacherAttributeDialogFragment.newInstance(ptmItem.teacherAttributes,ptmItem.ptmId)
            dialogFragment.setOnLocationUpdatedListener(onLocationUpdatedListener)

            dialogFragment.show(fragmentManager, "TeacherAttributeDialogFragment")
        }
    }

    override fun getItemCount(): Int {
        return ptmList.size
    }
}
